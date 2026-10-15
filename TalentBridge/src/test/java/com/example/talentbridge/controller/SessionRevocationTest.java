package com.example.talentbridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/** A stolen refresh token must not survive a password change or reset. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SessionRevocationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JdbcTemplate jdbc;

    final String sfx = String.valueOf(System.nanoTime());
    final String email = "revoke-" + sfx + "@test.com";
    final String password = "Passw0rd!x";
    final String meta = "{\"deviceName\":\"laptop\",\"deviceType\":\"web\",\"userAgent\":\"junit\"}";

    private MvcResult login() throws Exception {
        return mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"sessionMetaRequest\":" + meta + "}")).andReturn();
    }

    private String accessOf(MvcResult r) throws Exception {
        return objectMapper.readTree(r.getResponse().getContentAsString()).get("data").get("accessToken").asText();
    }

    private String refreshOf(MvcResult r) {
        return r.getResponse().getCookie("refresh_token").getValue();
    }

    private int refresh(String token) throws Exception {
        return mockMvc.perform(post("/auth/refresh-token").contentType(MediaType.APPLICATION_JSON).content(meta)
                .cookie(new MockCookie("refresh_token", token))).andReturn().getResponse().getStatus();
    }

    private void register() throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"name\":\"Revoke\"}"));
    }

    @Test
    @DisplayName("changing the password signs out every other session but keeps the current one")
    void changePasswordRevokesOtherSessions() throws Exception {
        register();
        MvcResult phone = login(), laptop = login();

        mockMvc.perform(post("/users/me/update-password").contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessOf(laptop)).cookie(new MockCookie("refresh_token", refreshOf(laptop)))
                        .content("{\"oldPassword\":\"" + password + "\",\"newPassword\":\"Newpassw0rd!\"}"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());

        assertEquals(401, refresh(refreshOf(phone)), "the other device is signed out");
        assertEquals(200, refresh(refreshOf(laptop)), "the device that changed the password stays signed in");
    }

    @Test
    @DisplayName("resetting the password through the emailed link signs out every session")
    void resetPasswordRevokesAllSessions() throws Exception {
        register();
        MvcResult phone = login();
        long userId = jdbc.queryForObject("SELECT id FROM users WHERE email = ?", Long.class, email);
        String raw = "revoke-token-" + sfx;
        String hash = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(raw.getBytes()));
        jdbc.update("INSERT INTO password_reset_tokens (token_hash, user_id, expiry_date, used, created_at) VALUES (?,?,?,?,?)",
                hash, userId, Timestamp.from(Instant.now().plusSeconds(600)), false, Timestamp.from(Instant.now()));

        mockMvc.perform(post("/auth/reset-password").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + raw + "\",\"password\":\"Resetpassw0rd!\",\"confirmPassword\":\"Resetpassw0rd!\"}"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());

        assertEquals(401, refresh(refreshOf(phone)));
    }
}
