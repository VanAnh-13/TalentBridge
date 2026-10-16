package com.example.talentbridge.realtime;

import com.example.talentbridge.TestTokens;
import com.example.talentbridge.notification.application.ReceiveNotificationRequest;
import com.example.talentbridge.realtime.application.PushToUser;
import com.example.talentbridge.realtime.application.port.Presence;
import com.example.talentbridge.shared.domain.exception.InvalidRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/** Real WebSocket round trips: connect with an access token, get things pushed live. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestTokens.class)
class RealtimeTest {

    static final AtomicLong ids = new AtomicLong(5000);

    @LocalServerPort int port;
    @Autowired ReceiveNotificationRequest receive;
    @Autowired PushToUser pushToUser;
    @Autowired Presence presence;

    @Test @DisplayName("a connected user receives their notification live, and only theirs")
    void notificationPushedLive() throws Exception {
        long me = ids.incrementAndGet(), stranger = ids.incrementAndGet();
        StompSession session = StompClient.connect(port, TestTokens.token(me, TestTokens.INBOX));
        BlockingQueue<Map<String, Object>> received = StompClient.listen(session, "/user/queue/notifications");
        assertTrue(presence.isOnline(me));
        assertFalse(presence.isOnline(stranger));

        receive.execute(UUID.randomUUID().toString(), stranger, "GENERAL", "for someone else", null, null, false);
        receive.execute(UUID.randomUUID().toString(), me, "GENERAL", "hello live", "body", "/x", false);

        Map<String, Object> message = received.poll(10, TimeUnit.SECONDS);
        assertNotNull(message, "nothing was pushed");
        assertEquals("hello live", message.get("title"));
        assertNotNull(message.get("id"));
        assertNull(received.poll(1, TimeUnit.SECONDS), "must not receive other people's notifications");
        session.disconnect();
    }

    @Test @DisplayName("'only if offline' is skipped while the user is connected (they saw it live)")
    void onlyIfOfflineSkippedWhenOnline() throws Exception {
        long me = ids.incrementAndGet();
        StompSession session = StompClient.connect(port, TestTokens.token(me, TestTokens.INBOX));
        BlockingQueue<Map<String, Object>> received = StompClient.listen(session, "/user/queue/notifications");
        assertTrue(receive.execute(UUID.randomUUID().toString(), me, "CHAT_MESSAGE", "New message", "hi", "/chat", true).isEmpty());
        assertNull(received.poll(1, TimeUnit.SECONDS));
        session.disconnect();
    }

    @Test @DisplayName("a live push reaches the user's channel; odd channel names are refused")
    void pushOnChannel() throws Exception {
        long me = ids.incrementAndGet();
        StompSession session = StompClient.connect(port, TestTokens.token(me, TestTokens.INBOX));
        BlockingQueue<Map<String, Object>> chat = StompClient.listen(session, "/user/queue/chat");
        pushToUser.execute(me, "chat", Map.of("conversationId", 7, "text", "hello"));
        Map<String, Object> message = chat.poll(10, TimeUnit.SECONDS);
        assertNotNull(message);
        assertEquals("hello", message.get("text"));
        assertThrows(InvalidRequestException.class, () -> pushToUser.execute(me, "../topic", Map.of()));
        session.disconnect();
    }

    @Test @DisplayName("connecting without a token from the auth service is refused")
    void connectNeedsTrustedToken() {
        assertThrows(Exception.class, () -> StompClient.connect(port, "not-a-token"));
        assertThrows(Exception.class, () -> StompClient.connect(port, TestTokens.foreignToken(1)));
    }

    @Test @DisplayName("a user cannot subscribe to destinations that are not their own queues")
    void cannotSubscribeElsewhere() throws Exception {
        StompSession session = StompClient.connect(port, TestTokens.token(ids.incrementAndGet(), TestTokens.INBOX));
        BlockingQueue<Map<String, Object>> leaked = StompClient.listen(session, "/queue/notifications");
        Thread.sleep(600);
        assertFalse(session.isConnected(), "a refused subscription ends the session");
        assertTrue(leaked.isEmpty());
    }
}
