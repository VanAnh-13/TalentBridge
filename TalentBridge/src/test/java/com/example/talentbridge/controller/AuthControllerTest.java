package com.example.talentbridge.controller;

import com.example.talentbridge.dto.request.auth.UserLoginRequestDto;
import com.example.talentbridge.dto.request.auth.UserRegisterRequestDto;
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
    @DisplayName("AUTH_01: Đăng ký user mới với thông tin hợp lệ")
    void testRegisterWithValidData() throws Exception {
        UserRegisterRequestDto request = new UserRegisterRequestDto();
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
    @DisplayName("AUTH_02: Đăng ký user với email đã tồn tại")
    void testRegisterWithDuplicateEmail() throws Exception {
        UserRegisterRequestDto request = new UserRegisterRequestDto();
        request.setEmail(testEmail);
        request.setPassword("AnotherPass@123");
        request.setName("Another User");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("đã tồn tại")));
    }

    @Test
    @Order(3)
    @DisplayName("AUTH_03: Đăng nhập với thông tin đúng")
    void testLoginWithValidCredentials() throws Exception {
        UserLoginRequestDto request = new UserLoginRequestDto();
        request.setEmail(testEmail);
        request.setPassword(testPassword);
        request.setSessionMetaRequest(new com.example.talentbridge.dto.request.auth.SessionMetaRequest("test-device", "test", "junit"));

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
    @DisplayName("AUTH_04: Đăng nhập với mật khẩu sai")
    void testLoginWithWrongPassword() throws Exception {
        UserLoginRequestDto request = new UserLoginRequestDto();
        request.setEmail(testEmail);
        request.setPassword("WrongPassword@123");
        request.setSessionMetaRequest(new com.example.talentbridge.dto.request.auth.SessionMetaRequest("test-device", "test", "junit"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("Invalid")));
    }

    @Test
    @Order(5)
    @DisplayName("AUTH_05: Đăng nhập với email không tồn tại")
    void testLoginWithNonExistentEmail() throws Exception {
        UserLoginRequestDto request = new UserLoginRequestDto();
        request.setEmail("nonexistent@test.com");
        request.setPassword("Password@123");
        request.setSessionMetaRequest(new com.example.talentbridge.dto.request.auth.SessionMetaRequest("test-device", "test", "junit"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @Order(6)
    @DisplayName("AUTH_09: Truy cập API với JWT token hợp lệ")
    void testAccessWithValidToken() throws Exception {
        mockMvc.perform(get("/auth/me")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(testEmail));
    }

    @Test
    @Order(7)
    @DisplayName("AUTH_10: Truy cập API với JWT token không hợp lệ")
    void testAccessWithInvalidToken() throws Exception {
        mockMvc.perform(get("/auth/me")
                .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(8)
    @DisplayName("AUTH_11: Truy cập API không có quyền")
    void testAccessWithoutPermission() throws Exception {
        // Try to access admin endpoint without admin role
        // Contract: the API answers 401 + ACCESS_DENIED (the frontend refreshes the token on it)
        mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));
    }

    @Test
    @Order(9)
    @DisplayName("AUTH_08: Logout và invalidate token")
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
