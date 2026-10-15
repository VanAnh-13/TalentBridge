package com.example.talentbridge.notification.infrastructure;

import com.example.talentbridge.notification.application.NotifyUser;
import com.example.talentbridge.user.application.UserDirectory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/** A real WebSocket round trip: connect with the access token, get a notification pushed live. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class NotificationPushTest {

    @LocalServerPort int port;
    @Autowired NotifyUser notifyUser;
    @Autowired UserDirectory users;
    @Autowired ObjectMapper objectMapper;

    private String loginToken(String email) {
        RestClient http = RestClient.create("http://localhost:" + port);
        http.post().uri("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .body("{\"email\":\"" + email + "\",\"password\":\"Passw0rd!x\",\"name\":\"Push\"}").retrieve().toBodilessEntity();
        Map<?, ?> res = http.post().uri("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .body("{\"email\":\"" + email + "\",\"password\":\"Passw0rd!x\",\"sessionMetaRequest\":{\"deviceName\":\"t\",\"deviceType\":\"t\",\"userAgent\":\"t\"}}")
                .retrieve().body(Map.class);
        return (String) ((Map<?, ?>) res.get("data")).get("accessToken");
    }

    private WebSocketStompClient client() {
        WebSocketStompClient client = new WebSocketStompClient(new StandardWebSocketClient());
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        client.setMessageConverter(converter);
        return client;
    }

    @Test
    @DisplayName("a connected user receives their notification live, and only theirs")
    void pushedLive() throws Exception {
        String email = "push-" + System.nanoTime() + "@test.com";
        String token = loginToken(email);
        Long id = users.findByEmail(email).orElseThrow().id();

        StompHeaders connect = new StompHeaders();
        connect.add("Authorization", "Bearer " + token);
        StompSession session = client().connectAsync("ws://localhost:" + port + "/ws", new WebSocketHttpHeaders(),
                connect, new StompSessionHandlerAdapter() {}).get(10, TimeUnit.SECONDS);

        BlockingQueue<Map<String, Object>> received = new LinkedBlockingQueue<>();
        session.subscribe("/user/queue/notifications", new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders headers) { return Map.class; }
            @Override @SuppressWarnings("unchecked")
            public void handleFrame(StompHeaders headers, Object payload) { received.add((Map<String, Object>) payload); }
        });
        Thread.sleep(500);   // let the subscription register

        String stranger = "stranger-" + System.nanoTime() + "@test.com";
        loginToken(stranger);
        notifyUser.execute(users.findByEmail(stranger).orElseThrow().id(), "GENERAL", "for someone else", null, null);
        notifyUser.execute(id, "GENERAL", "hello live", "body", "/x");

        Map<String, Object> message = received.poll(10, TimeUnit.SECONDS);
        assertNotNull(message, "nothing was pushed");
        assertEquals("hello live", message.get("title"));
        assertNull(received.poll(1, TimeUnit.SECONDS), "must not receive other people's notifications");
        session.disconnect();
    }

    @Test
    @DisplayName("connecting without a valid access token is refused")
    void connectNeedsToken() {
        StompHeaders connect = new StompHeaders();
        connect.add("Authorization", "Bearer not-a-token");
        assertThrows(Exception.class, () -> client().connectAsync("ws://localhost:" + port + "/ws",
                new WebSocketHttpHeaders(), connect, new StompSessionHandlerAdapter() {}).get(10, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("a user cannot subscribe to destinations that are not their own queues")
    void cannotSubscribeElsewhere() throws Exception {
        String token = loginToken("sub-" + System.nanoTime() + "@test.com");
        StompHeaders connect = new StompHeaders();
        connect.add("Authorization", "Bearer " + token);
        CompletableFuture<Throwable> failure = new CompletableFuture<>();
        StompSession session = client().connectAsync("ws://localhost:" + port + "/ws", new WebSocketHttpHeaders(),
                connect, new StompSessionHandlerAdapter() {
                    @Override public void handleTransportError(StompSession s, Throwable ex) { failure.complete(ex); }
                    @Override public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) { failure.complete(ex); }
                }).get(10, TimeUnit.SECONDS);
        BlockingQueue<Object> leaked = new LinkedBlockingQueue<>();
        session.subscribe("/queue/notifications", new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders headers) { return Map.class; }
            @Override public void handleFrame(StompHeaders headers, Object payload) { leaked.add(payload); }
        });
        // the broker closes the session on the refused subscription
        Thread.sleep(1000);
        assertFalse(session.isConnected(), "a refused subscription ends the session");
        assertTrue(leaked.isEmpty());
    }
}
