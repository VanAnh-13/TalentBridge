package com.example.talentbridge.notification;

import com.example.talentbridge.TestTokens;
import com.example.talentbridge.notification.application.ReceiveNotificationRequest;
import com.example.talentbridge.notification.domain.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestTokens.class)
class NotificationApiTest {

    static final AtomicLong ids = new AtomicLong(1000);

    @Autowired MockMvc mockMvc;
    @Autowired ReceiveNotificationRequest receive;

    private Notification notify(long userId, String title, String link) {
        return receive.execute(UUID.randomUUID().toString(), userId, "GENERAL", title, "body", link, false).orElseThrow();
    }

    @Test
    @DisplayName("inbox: newest first, unread count, mark one / all as read, never someone else's")
    void inbox() throws Exception {
        long me = ids.incrementAndGet(), other = ids.incrementAndGet();
        String mine = "Bearer " + TestTokens.token(me, TestTokens.INBOX), theirs = "Bearer " + TestTokens.token(other, TestTokens.INBOX);

        Notification first = notify(me, "first", "/home");
        notify(me, "second", null);
        Notification foreign = notify(other, "not yours", null);

        mockMvc.perform(get("/notifications").header("Authorization", mine))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].title").value("second"))
                .andExpect(jsonPath("$.data.content[1].link").value("/home"))
                .andExpect(jsonPath("$.data.content[0].read").value(false));
        mockMvc.perform(get("/notifications/unread-count").header("Authorization", mine))
                .andExpect(jsonPath("$.data.count").value(2));

        // someone else's notification looks like it does not exist
        mockMvc.perform(patch("/notifications/" + foreign.getId() + "/read").header("Authorization", mine))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/notifications/" + first.getId() + "/read").header("Authorization", mine))
                .andExpect(status().isNoContent());
        mockMvc.perform(patch("/notifications/" + first.getId() + "/read").header("Authorization", mine))
                .andExpect(status().isNoContent());   // idempotent
        mockMvc.perform(get("/notifications").param("unread", "true").header("Authorization", mine))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("second"));

        mockMvc.perform(post("/notifications/read-all").header("Authorization", mine))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.updated").value(1));
        mockMvc.perform(get("/notifications/unread-count").header("Authorization", mine))
                .andExpect(jsonPath("$.data.count").value(0));
        mockMvc.perform(get("/notifications/unread-count").header("Authorization", theirs))
                .andExpect(jsonPath("$.data.count").value(1));
    }

    @Test
    @DisplayName("paging follows the API contract of the other services: ?page is 1-based, pages hold at most 20")
    void paging() throws Exception {
        long me = ids.incrementAndGet();
        String token = "Bearer " + TestTokens.token(me, TestTokens.INBOX);
        for (int i = 1; i <= 3; i++) notify(me, "n" + i, null);
        mockMvc.perform(get("/notifications").param("page", "1").param("size", "2").header("Authorization", token))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].title").value("n3"))
                .andExpect(jsonPath("$.data.totalPages").value(2));
        mockMvc.perform(get("/notifications").param("page", "2").param("size", "2").header("Authorization", token))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("n1"));
        mockMvc.perform(get("/notifications").param("size", "500").header("Authorization", token))
                .andExpect(jsonPath("$.data.size").value(20));
    }

    @Test
    @DisplayName("only tokens from the auth service, with the right permission, are accepted")
    void security() throws Exception {
        long me = ids.incrementAndGet();
        mockMvc.perform(get("/notifications")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("UNAUTHORIZED"));
        mockMvc.perform(get("/notifications").header("Authorization", "Bearer " + TestTokens.foreignToken(me)))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/notifications").header("Authorization", "Bearer " + TestTokens.token(me, List.of("GET /jobs"))))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("the same event delivered twice leaves one notification")
    void idempotent() throws Exception {
        long me = ids.incrementAndGet();
        String eventId = UUID.randomUUID().toString();
        assertTrue(receive.execute(eventId, me, "GENERAL", "once", null, null, false).isPresent());
        assertTrue(receive.execute(eventId, me, "GENERAL", "once", null, null, false).isEmpty());
        mockMvc.perform(get("/notifications/unread-count").header("Authorization", "Bearer " + TestTokens.token(me, TestTokens.INBOX)))
                .andExpect(jsonPath("$.data.count").value(1));
    }

    @Test
    @DisplayName("'only if offline' is stored for someone who is not connected")
    void offlineIsStored() {
        long me = ids.incrementAndGet();
        assertTrue(receive.execute(UUID.randomUUID().toString(), me, "CHAT_MESSAGE", "Tin nhắn mới", "hi", "/chat", true).isPresent());
    }
}
