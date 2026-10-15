package com.example.talentbridge.chat;

import com.example.talentbridge.controller.TestHelper;
import com.example.talentbridge.events.RealtimePush;
import com.example.talentbridge.shared.infrastructure.messaging.OutboxEventJpaRepository;
import com.example.talentbridge.support.OutboxProbe;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Chat asks the notification service (which holds the browsers' WebSocket connections) to deliver messages and read
 * receipts live, and to notify people who are offline. The events are checked in the outbox; delivery itself is
 * covered by the notification service's tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChatEventsTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired OutboxEventJpaRepository outbox;

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

    @Test
    @DisplayName("messages and read receipts are pushed to the other people only; offline notification is asked once")
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
        long aId = id(call(get("/auth/me"), a, null)), bId = id(call(get("/auth/me"), b, null)), cId = id(call(get("/auth/me"), c, null));
        long resume = id(mockMvc.perform(multipart(HttpMethod.POST, "/resumes")
                .file(new MockMultipartFile("resume", "r.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"email\":\"" + cMail + "\",\"status\":\"PENDING\",\"user\":{\"id\":" + cId + "},\"job\":{\"id\":" + job + "}}").getBytes()))
                .file(new MockMultipartFile("pdfFile", "cv.pdf", "application/pdf", "%PDF-1.4 cv".getBytes()))
                .header("Authorization", "Bearer " + c)).andExpect(status().isCreated()));
        long conversation = id(call(post("/chats/resumes/" + resume), c, null));
        OutboxProbe probe = new OutboxProbe(outbox);

        long messageId = id(call(post("/chats/" + conversation + "/messages"), c, "{\"text\":\"hello live\"}").andExpect(status().isCreated()));
        call(post("/chats/" + conversation + "/messages"), c, "{\"text\":\"second\"}").andExpect(status().isCreated());

        for (long recruiter : new long[]{aId, bId}) {
            List<RealtimePush> pushed = probe.pushesFor(recruiter, "chat");
            assertEquals(2, pushed.size(), "every message is pushed live");
            JsonNode first = pushed.get(0).data();
            assertEquals("hello live", first.get("text").asText());
            assertEquals(conversation, first.get("conversationId").asLong());
            assertEquals(messageId, first.get("id").asLong());
            assertTrue(first.get("senderName").asText().startsWith("pushc"));
            assertTrue(first.hasNonNull("sentAt"));
            var told = probe.notificationsFor(recruiter, "CHAT_MESSAGE");
            assertEquals(1, told.size(), "one offline notification for the run of unread messages");
            assertTrue(told.get(0).onlyIfOffline());
        }
        assertTrue(probe.pushesFor(cId, "chat").isEmpty(), "the sender is not pushed their own message");

        // reading shows up at the other end as a receipt
        call(post("/chats/" + conversation + "/read"), a, null).andExpect(status().isNoContent());
        List<RealtimePush> receipts = probe.pushesFor(cId, "chat-read");
        assertEquals(1, receipts.size());
        assertEquals(aId, receipts.get(0).data().get("readerId").asLong());
        assertTrue(receipts.get(0).data().get("upToMessageId").asLong() > messageId);
    }
}
