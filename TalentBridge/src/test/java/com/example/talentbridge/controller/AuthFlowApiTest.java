package com.example.talentbridge.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Characterisation of registration, login, token refresh, sessions and password reset. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthFlowApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JdbcTemplate jdbc;

    final String sfx = String.valueOf(System.nanoTime());
    final String email = "flow-" + sfx + "@test.com";
    final String password = "Passw0rd!x";
    final String meta = "{\"deviceName\":\"laptop\",\"deviceType\":\"web\",\"userAgent\":\"junit\"}";
    String access, refresh, refresh2;
    long userId;

    private ResultActions call(MockHttpServletRequestBuilder b, String token, String body) throws Exception {
        if (token != null) b.header("Authorization", "Bearer " + token);
        if (body != null) b.contentType(MediaType.APPLICATION_JSON).content(body);
        return mockMvc.perform(b);
    }

    private MvcResult login(String e, String p) throws Exception {
        return mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + e + "\",\"password\":\"" + p + "\",\"sessionMetaRequest\":" + meta + "}")).andReturn();
    }

    private String hash(String raw) throws Exception {
        return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(raw.getBytes()));
    }

    private void insertResetToken(String raw, boolean used, Instant expiry) throws Exception {
        jdbc.update("INSERT INTO password_reset_tokens (token_hash, user_id, expiry_date, used, created_at) VALUES (?,?,?,?,?)",
                hash(raw), userId, Timestamp.from(expiry), used, Timestamp.from(Instant.now().minusSeconds(3600)));
    }

    // ---------------------------------------------------------------- register / login
    @Test @Order(1) @DisplayName("register: USER role by default, RECRUITER on request, duplicate -> 409, invalid -> 400")
    void register() throws Exception {
        call(post("/auth/register"), null, "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"name\":\"Flow\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.name").value("Flow"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.data.permissions", hasItem("GET /resumes/me")))
                .andExpect(jsonPath("$.data.companyId").doesNotExist())
                .andExpect(jsonPath("$.data.id").isNumber());
        call(post("/auth/register"), null, "{\"email\":\"r-" + email + "\",\"password\":\"" + password + "\",\"name\":\"R\",\"recruiter\":true}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.role").value("RECRUITER"));
        call(post("/auth/register"), null, "{\"email\":\"" + email + "\",\"password\":\"x\",\"name\":\"Dup\"}")
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("Email đã tồn tại"));
        call(post("/auth/register"), null, "{\"email\":\"bad\",\"password\":\"\",\"name\":\"\"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test @Order(2) @DisplayName("login: token + http-only refresh cookie + user info; bad credentials -> 401")
    void loginOk() throws Exception {
        MvcResult r = login(email, password);
        assertEquals(200, r.getResponse().getStatus());
        JsonNode body = objectMapper.readTree(r.getResponse().getContentAsString()).get("data");
        access = body.get("accessToken").asText();
        userId = body.get("user").get("id").asLong();
        assertEquals(email, body.get("user").get("email").asText());
        assertEquals("USER", body.get("user").get("role").asText());

        String setCookie = r.getResponse().getHeader("Set-Cookie");
        assertNotNull(setCookie);
        assertTrue(setCookie.startsWith("refresh_token="), setCookie);
        assertTrue(setCookie.contains("HttpOnly"));
        assertTrue(setCookie.contains("SameSite=Strict"));
        assertTrue(setCookie.contains("Max-Age=86400"), setCookie);
        refresh = r.getResponse().getCookie("refresh_token").getValue();

        MvcResult bad = login(email, "wrong-password");
        assertEquals(401, bad.getResponse().getStatus());
        assertTrue(bad.getResponse().getContentAsString().contains("BAD_CREDENTIALS"));
        assertEquals(401, login("nobody-" + sfx + "@test.com", password).getResponse().getStatus());
        call(post("/auth/login"), null, "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                .andExpect(status().isBadRequest());
    }

    @Test @Order(3) @DisplayName("the access token carries the subject, the user and the permissions")
    void tokenClaims() throws Exception {
        JsonNode claims = objectMapper.readTree(Base64.getUrlDecoder().decode(access.split("\\.")[1]));
        assertEquals(email, claims.get("sub").asText());
        assertEquals(email, claims.get("user").get("email").asText());
        assertEquals("USER", claims.get("user").get("role").asText());
        assertTrue(claims.get("permissions").isArray());
        assertTrue(claims.get("permissions").toString().contains("GET /resumes/me"));
        assertTrue(claims.get("exp").asLong() > claims.get("iat").asLong());
    }

    @Test @Order(4) @DisplayName("/auth/me and /auth/me/details")
    void me() throws Exception {
        call(get("/auth/me"), access, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.data.updatedAt").isString());
        call(get("/auth/me/details"), access, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.name").value("Flow"))
                .andExpect(jsonPath("$.data.createdAt").exists());
        call(get("/auth/me"), "garbage", null).andExpect(status().isUnauthorized());
        call(get("/auth/me"), null, null).andExpect(status().isUnauthorized());
    }

    // ---------------------------------------------------------------- refresh / sessions / logout
    @Test @Order(5) @DisplayName("refresh rotates the refresh token: the old one stops working")
    void refreshRotates() throws Exception {
        Thread.sleep(1100);   // legacy tokens carry no unique id: two issued within one second are identical
        MvcResult r = mockMvc.perform(post("/auth/refresh-token").cookie(new MockCookie("refresh_token", refresh))
                .contentType(MediaType.APPLICATION_JSON).content(meta)).andReturn();
        assertEquals(200, r.getResponse().getStatus(), r.getResponse().getContentAsString());
        assertNotNull(objectMapper.readTree(r.getResponse().getContentAsString()).get("data").get("accessToken"));
        refresh2 = r.getResponse().getCookie("refresh_token").getValue();
        assertNotEquals(refresh, refresh2);

        mockMvc.perform(post("/auth/refresh-token").cookie(new MockCookie("refresh_token", refresh))
                .contentType(MediaType.APPLICATION_JSON).content(meta)).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/auth/refresh-token").contentType(MediaType.APPLICATION_JSON).content(meta))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.errorCode").value("MISSING_COOKIE"));
        mockMvc.perform(post("/auth/refresh-token").cookie(new MockCookie("refresh_token", "not.a.jwt"))
                .contentType(MediaType.APPLICATION_JSON).content(meta)).andExpect(status().isUnauthorized());
    }

    @Test @Order(6) @DisplayName("sessions: list marks the current one; a session can be revoked; others' sessions cannot")
    void sessions() throws Exception {
        Thread.sleep(1100);   // legacy tokens carry no unique id: a login within the same second would reuse the token
        MvcResult second = login(email, password);
        String other = second.getResponse().getCookie("refresh_token").getValue();

        MvcResult list = mockMvc.perform(get("/auth/sessions").header("Authorization", "Bearer " + access)
                .cookie(new MockCookie("refresh_token", refresh2))).andExpect(status().isOk()).andReturn();
        JsonNode sessions = objectMapper.readTree(list.getResponse().getContentAsString()).get("data");
        assertEquals(2, sessions.size());
        int current = 0;
        String otherSessionId = null;
        for (JsonNode s : sessions) {
            assertEquals("laptop", s.get("deviceName").asText());
            if (s.get("current").asBoolean()) current++; else otherSessionId = s.get("sessionId").asText();
        }
        assertEquals(1, current);

        // somebody else cannot revoke it
        call(post("/auth/register"), null, "{\"email\":\"intruder-" + email + "\",\"password\":\"" + password + "\",\"name\":\"I\"}").andExpect(status().isOk());
        String intruder = objectMapper.readTree(login("intruder-" + email, password).getResponse().getContentAsString()).get("data").get("accessToken").asText();
        call(delete("/auth/sessions/" + otherSessionId), intruder, null).andExpect(status().isForbidden());

        call(delete("/auth/sessions/" + otherSessionId), access, null).andExpect(status().isOk());
        mockMvc.perform(post("/auth/refresh-token").cookie(new MockCookie("refresh_token", other))
                .contentType(MediaType.APPLICATION_JSON).content(meta)).andExpect(status().isUnauthorized());
    }

    @Test @Order(7) @DisplayName("logout clears the cookie and invalidates the refresh token (even without a cookie)")
    void logout() throws Exception {
        MvcResult r = mockMvc.perform(post("/auth/logout").cookie(new MockCookie("refresh_token", refresh2))).andReturn();
        assertEquals(200, r.getResponse().getStatus());
        assertTrue(r.getResponse().getHeader("Set-Cookie").contains("Max-Age=0"));
        mockMvc.perform(post("/auth/refresh-token").cookie(new MockCookie("refresh_token", refresh2))
                .contentType(MediaType.APPLICATION_JSON).content(meta)).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/auth/logout")).andExpect(status().isOk());
    }

    // ---------------------------------------------------------------- password reset
    @Test @Order(8) @DisplayName("forgot-password always answers 200 (no account enumeration)")
    void forgotPassword() throws Exception {
        call(post("/auth/forgot-password"), null, "{\"email\":\"" + email + "\"}").andExpect(status().isOk());
        call(post("/auth/forgot-password"), null, "{\"email\":\"" + email + "\"}").andExpect(status().isOk());  // rate limited
        call(post("/auth/forgot-password"), null, "{\"email\":\"nobody-" + sfx + "@test.com\"}").andExpect(status().isOk());
        call(post("/auth/forgot-password"), null, "{\"email\":\"not-an-email\"}").andExpect(status().isBadRequest());
        // exactly one token was created for the account, and it is stored hashed
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM password_reset_tokens WHERE user_id = ?", Integer.class, userId);
        assertEquals(1, n);
    }

    @Test @Order(9) @DisplayName("verify: valid / used / expired / unknown")
    void verifyToken() throws Exception {
        insertResetToken("valid-token-" + sfx, false, Instant.now().plusSeconds(600));
        insertResetToken("used-token-" + sfx, true, Instant.now().plusSeconds(600));
        insertResetToken("expired-token-" + sfx, false, Instant.now().minusSeconds(600));

        mockMvc.perform(get("/auth/reset-password/verify").param("token", "valid-token-" + sfx)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(true));
        mockMvc.perform(get("/auth/reset-password/verify").param("token", "used-token-" + sfx)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(false)).andExpect(jsonPath("$.data.errorCode").value("TOKEN_USED"));
        mockMvc.perform(get("/auth/reset-password/verify").param("token", "expired-token-" + sfx)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.errorCode").value("TOKEN_EXPIRED"));
        mockMvc.perform(get("/auth/reset-password/verify").param("token", "unknown-" + sfx)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.errorCode").value("TOKEN_INVALID"));
    }

    @Test @Order(10) @DisplayName("reset: success changes the password and consumes every pending token")
    void resetPassword() throws Exception {
        String t = "valid-token-" + sfx;
        String newPassword = "Brandnew1";
        // rejected: mismatch, weak password, used, expired, unknown (the statuses of these rejections are asserted separately)
        int mismatch = reset(t, newPassword, "Different1");
        int weak = reset(t, "weak", "weak");
        int used = reset("used-token-" + sfx, newPassword, newPassword);
        int expired = reset("expired-token-" + sfx, newPassword, newPassword);
        int unknown = reset("unknown-" + sfx, newPassword, newPassword);
        assertEquals(REJECTION_STATUS, mismatch);
        assertEquals(400, weak);                     // "weak" is shorter than 8 characters: bean validation
        assertEquals(REJECTION_STATUS, used);
        assertEquals(REJECTION_STATUS, expired);
        assertEquals(REJECTION_STATUS, unknown);

        assertEquals(200, reset(t, newPassword, newPassword));
        assertEquals(401, login(email, password).getResponse().getStatus());
        assertEquals(200, login(email, newPassword).getResponse().getStatus());
        assertEquals(REJECTION_STATUS, reset(t, newPassword, newPassword));          // single use
        Integer pending = jdbc.queryForObject("SELECT COUNT(*) FROM password_reset_tokens WHERE user_id = ? AND used = false", Integer.class, userId);
        assertEquals(0, pending);
    }

    /** Status of a rejected reset: the legacy code let an IllegalArgumentException escape (HTTP 500). */
    static final int REJECTION_STATUS = 500;

    private int reset(String token, String password, String confirm) throws Exception {
        try {
            return mockMvc.perform(post("/auth/reset-password").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"token\":\"" + token + "\",\"password\":\"" + password + "\",\"confirmPassword\":\"" + confirm + "\"}"))
                    .andReturn().getResponse().getStatus();
        } catch (jakarta.servlet.ServletException unhandled) {
            return 500;   // what a real server answers for an exception nobody handles
        }
    }
}
