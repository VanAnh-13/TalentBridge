package com.example.talentbridge.notification.presentation;

import com.example.talentbridge.controller.TestHelper;
import com.example.talentbridge.notification.application.NotifyUser;
import com.example.talentbridge.user.application.UserDirectory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired NotifyUser notifyUser;
    @Autowired UserDirectory users;

    private String register(String email) throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"Passw0rd!x\",\"name\":\"N\"}")).andExpect(status().isOk());
        return "Bearer " + TestHelper.getUserToken(mockMvc, objectMapper, email, "Passw0rd!x");
    }

    @Test
    @DisplayName("inbox: newest first, unread count, mark one / all as read, never someone else's")
    void inbox() throws Exception {
        String sfx = String.valueOf(System.nanoTime());
        String mine = register("inbox-a-" + sfx + "@test.com"), other = register("inbox-b-" + sfx + "@test.com");
        Long myId = users.findByEmail("inbox-a-" + sfx + "@test.com").orElseThrow().id();
        Long otherId = users.findByEmail("inbox-b-" + sfx + "@test.com").orElseThrow().id();

        var first = notifyUser.execute(myId, "GENERAL", "first", "body", "/home");
        notifyUser.execute(myId, "GENERAL", "second", null, null);
        var foreign = notifyUser.execute(otherId, "GENERAL", "not yours", null, null);

        mockMvc.perform(get("/notifications")).andExpect(status().isUnauthorized());

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
        mockMvc.perform(get("/notifications/unread-count").header("Authorization", other))
                .andExpect(jsonPath("$.data.count").value(1));

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
        mockMvc.perform(get("/notifications/unread-count").header("Authorization", other))
                .andExpect(jsonPath("$.data.count").value(1));
    }

    @Test
    @DisplayName("the browser may send PATCH from the web origin (CORS preflight)")
    void patchIsAllowedCrossOrigin() throws Exception {
        mockMvc.perform(options("/notifications/1/read")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "PATCH")
                        .header("Access-Control-Request-Headers", "authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("PATCH")));
    }
}
