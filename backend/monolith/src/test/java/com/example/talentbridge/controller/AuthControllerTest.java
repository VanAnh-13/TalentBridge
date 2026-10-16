package com.example.talentbridge.controller;

import com.example.talentbridge.auth.presentation.LoginRequest;
import com.example.talentbridge.auth.presentation.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;
    private String refreshToken;
    private String testEmail = "test_" + System.currentTimeMillis() + "@test.com";
    private String testPassword = "Test@123456";

    @Test
    @Order(1)
    @DisplayName("AUTH_01: register a new user with valid details")
    void testRegisterWithValidData() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(testEmail);
        request.setPassword(testPassword);
        request.setName("Test User");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(testEmail))
                .andExpect(jsonPath("$.data.name").value("Test User"));
    }

    @Test
    @Order(2)
    @DisplayName("AUTH_02: register with an email that already exists")
    void testRegisterWithDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(testEmail);
        request.setPassword("AnotherPass@123");
        request.setName("Another User");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @Order(3)
    @DisplayName("AUTH_03: log in with correct credentials")
    void testLoginWithValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(testEmail);
        request.setPassword(testPassword);
        request.setSessionMetaRequest(new com.example.talentbridge.auth.presentation.SessionMetaRequest("test-device", "test", "junit"));

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode jsonResponse = objectMapper.readTree(response);
        accessToken = jsonResponse.get("data").get("accessToken").asText();
        
        // Get refresh token from cookie
        refreshToken = result.getResponse().getCookie("refresh_token").getValue();
    }

    @Test
    @Order(4)
    @DisplayName("AUTH_04: log in with a wrong password")
    void testLoginWithWrongPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(testEmail);
        request.setPassword("WrongPassword@123");
        request.setSessionMetaRequest(new com.example.talentbridge.auth.presentation.SessionMetaRequest("test-device", "test", "junit"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("Invalid")));
    }

    @Test
    @Order(5)
    @DisplayName("AUTH_05: log in with an unknown email")
    void testLoginWithNonExistentEmail() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("nonexistent@test.com");
        request.setPassword("Password@123");
        request.setSessionMetaRequest(new com.example.talentbridge.auth.presentation.SessionMetaRequest("test-device", "test", "junit"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @Order(6)
    @DisplayName("AUTH_09: call the API with a valid JWT")
    void testAccessWithValidToken() throws Exception {
        mockMvc.perform(get("/auth/me")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(testEmail));
    }

    @Test
    @Order(7)
    @DisplayName("AUTH_10: call the API with an invalid JWT")
    void testAccessWithInvalidToken() throws Exception {
        mockMvc.perform(get("/auth/me")
                .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(8)
    @DisplayName("AUTH_11: call the API without permission")
    void testAccessWithoutPermission() throws Exception {
        // Try to access admin endpoint without admin role
        // Authenticated but lacking the permission -> 403 (401 is reserved for missing/invalid credentials)
        mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));
    }

    @Test
    @Order(9)
    @DisplayName("AUTH_08: log out and invalidate the token")
    void testLogout() throws Exception {
        mockMvc.perform(post("/auth/logout")
                .cookie(new org.springframework.mock.web.MockCookie("refresh_token", refreshToken)))
                .andExpect(status().isOk());

        // Access tokens are stateless JWTs; logout invalidates the refresh token instead
        mockMvc.perform(post("/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"deviceName\":\"test-device\",\"deviceType\":\"test\",\"userAgent\":\"junit\"}")
                .cookie(new org.springframework.mock.web.MockCookie("refresh_token", refreshToken)))
                .andExpect(status().isUnauthorized());
    }
}
