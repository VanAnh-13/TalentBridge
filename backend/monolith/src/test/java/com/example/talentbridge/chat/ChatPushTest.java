package com.example.talentbridge.chat;

import com.example.talentbridge.controller.TestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Chat over a real WebSocket: connected people get messages live, absent people get a notification instead. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChatPushTest {

    @LocalServerPort int port;
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String token(String email) throws Exception {
        return TestHelper.getUserToken(mockMvc, objectMapper, email, "Passw0rd!x");
    }

    private void register(String email, boolean recruiter) throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"Passw0rd!x\",\"name\":\"" + email.split("-")[0] + "\",\"recruiter\":" + recruiter + "}"))
                .andExpect(status().isOk());
    }

    private ResultActions call(MockHttpServletRequestBuilder b, String token, String body) throws Exception {
        b.header("Authorization", "Bearer " + token);
        if (body != null) b.contentType(MediaType.APPLICATION_JSON).content(body);
        return mockMvc.perform(b);
    }

    private long id(ResultActions r) throws Exception {
        return objectMapper.readTree(r.andReturn().getResponse().getContentAsString()).get("data").get("id").asLong();
    }

    /** A connected STOMP session with queues collecting what arrives on /user/queue/chat and /user/queue/chat-read. */
    static class Listener {
        final BlockingQueue<Map<String, Object>> messages = new LinkedBlockingQueue<>();
        final BlockingQueue<Map<String, Object>> receipts = new LinkedBlockingQueue<>();
        StompSession session;
    }

    private Listener connect(String accessToken) throws Exception {
        WebSocketStompClient client = new WebSocketStompClient(new StandardWebSocketClient());
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        client.setMessageConverter(converter);
        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        Listener l = new Listener();
        l.session = client.connectAsync("ws://localhost:" + port + "/ws", new WebSocketHttpHeaders(), headers,
                new StompSessionHandlerAdapter() {}).get(10, TimeUnit.SECONDS);
        subscribe(l.session, "/user/queue/chat", l.messages);
        subscribe(l.session, "/user/queue/chat-read", l.receipts);
        return l;
    }

    @SuppressWarnings("unchecked")
    private static void subscribe(StompSession session, String destination, BlockingQueue<Map<String, Object>> into) {
        session.subscribe(destination, new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders headers) { return Map.class; }
            @Override public void handleFrame(StompHeaders headers, Object payload) { into.add((Map<String, Object>) payload); }
        });
    }

    @Test
    @DisplayName("messages and read receipts arrive live for connected people; an absent colleague is notified instead")
    void live() throws Exception {
        String sfx = String.valueOf(System.nanoTime());
        String admin = TestHelper.getAdminToken(mockMvc, objectMapper);
        long skill = id(call(post("/skills"), admin, "{\"name\":\"PushSkill-" + sfx + "\"}"));
        String aMail = "pusha-" + sfx + "@test.com", bMail = "pushb-" + sfx + "@test.com", cMail = "pushc-" + sfx + "@test.com";
        register(aMail, true); register(bMail, true); register(cMail, false);
        String a = token(aMail);
        long company = id(mockMvc.perform(multipart(HttpMethod.POST, "/companies/me")
                .file(new MockMultipartFile("company", "c.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"name\":\"PushCo " + sfx + "\",\"description\":\"d\",\"address\":\"a\"}").getBytes()))
                .header("Authorization", "Bearer " + a)).andExpect(status().isCreated()));
        a = token(aMail);
        call(post("/companies/me/users"), a, "{\"email\":\"" + bMail + "\"}").andExpect(status().is2xxSuccessful());
        String b = token(bMail), c = token(cMail);
        long job = id(call(post("/jobs/company"), a, "{\"name\":\"Push Dev\",\"location\":\"HCM\",\"salary\":1,\"quantity\":1,\"level\":\"FRESHER\","
                + "\"description\":\"d\",\"startDate\":\"2030-01-01T00:00:00Z\",\"endDate\":\"2030-06-01T00:00:00Z\",\"active\":true,"
                + "\"company\":{\"id\":" + company + "},\"skills\":[{\"id\":" + skill + "}]}"));
        long candId = id(call(get("/auth/me"), c, null));
        long resume = id(mockMvc.perform(multipart(HttpMethod.POST, "/resumes")
                .file(new MockMultipartFile("resume", "r.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"email\":\"" + cMail + "\",\"status\":\"PENDING\",\"user\":{\"id\":" + candId + "},\"job\":{\"id\":" + job + "}}").getBytes()))
                .file(new MockMultipartFile("pdfFile", "cv.pdf", "application/pdf", "%PDF-1.4 cv".getBytes()))
                .header("Authorization", "Bearer " + c)).andExpect(status().isCreated()));
        long conversation = id(call(post("/chats/resumes/" + resume), c, null));

        Listener candidate = connect(c), recruiterA = connect(a);   // recruiter B stays offline
        Thread.sleep(500);

        call(post("/chats/" + conversation + "/messages"), c, "{\"text\":\"hello live\"}").andExpect(status().isCreated());

        Map<String, Object> pushed = recruiterA.messages.poll(10, TimeUnit.SECONDS);
        assertNotNull(pushed, "the connected recruiter must receive the message");
        assertEquals("hello live", pushed.get("text"));
        assertEquals(((Number) pushed.get("conversationId")).longValue(), conversation);
        assertTrue(String.valueOf(pushed.get("senderName")).startsWith("pushc"));
        assertNull(candidate.messages.poll(1, TimeUnit.SECONDS), "the sender is not pushed their own message");

        // the absent colleague was notified instead
        mockMvc.perform(get("/notifications").header("Authorization", "Bearer " + b))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data.content[0].type").value("CHAT_MESSAGE"));
        // the connected one was not
        mockMvc.perform(get("/notifications").header("Authorization", "Bearer " + a))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data.content[?(@.type=='CHAT_MESSAGE')]", org.hamcrest.Matchers.empty()));

        // reading shows up at the other end as a receipt
        call(post("/chats/" + conversation + "/read"), a, null).andExpect(status().isNoContent());
        Map<String, Object> receipt = candidate.receipts.poll(10, TimeUnit.SECONDS);
        assertNotNull(receipt, "the candidate must see that the message was read");
        assertEquals(((Number) receipt.get("upToMessageId")).longValue(), ((Number) pushed.get("id")).longValue());

        candidate.session.disconnect();
        recruiterA.session.disconnect();
    }
}
