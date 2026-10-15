package com.example.talentbridge.controller;

import com.example.talentbridge.dto.request.auth.UserLoginRequestDto;
import com.example.talentbridge.dto.request.job.JobRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TestHelper {

    // Admin credentials - should be seeded in test database
    private static final String ADMIN_EMAIL = "test-admin@talentbridge.com";
    private static final String ADMIN_PASSWORD = "Admin@123456";

    public static String getAdminToken(MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        UserLoginRequestDto loginDto = new UserLoginRequestDto();
        loginDto.setEmail(ADMIN_EMAIL);
        loginDto.setPassword(ADMIN_PASSWORD);
        loginDto.setSessionMetaRequest(new com.example.talentbridge.dto.request.auth.SessionMetaRequest("test-device", "test", "junit"));

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("data").get("accessToken").asText();
    }

    public static String getUserToken(MockMvc mockMvc, ObjectMapper objectMapper, String email, String password) throws Exception {
        UserLoginRequestDto loginDto = new UserLoginRequestDto();
        loginDto.setEmail(email);
        loginDto.setPassword(password);
        loginDto.setSessionMetaRequest(new com.example.talentbridge.dto.request.auth.SessionMetaRequest("test-device", "test", "junit"));

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("data").get("accessToken").asText();
    }

    public static Long createJobForCompany(MockMvc mockMvc, ObjectMapper objectMapper, String token, Long companyId, Long skillId) throws Exception {
        JobRequestDto jobDto = new JobRequestDto();
        jobDto.setName("Test Job " + System.currentTimeMillis());
        jobDto.setLocation("Test Location");
        jobDto.setDescription("Test Job Description");
        jobDto.setSalary(2000.0);
        jobDto.setQuantity(5);
        jobDto.setLevel(com.example.talentbridge.model.constant.Level.FRESHER);
        jobDto.setStartDate(Instant.now());
        jobDto.setEndDate(Instant.now().plus(30, ChronoUnit.DAYS));
        jobDto.setActive(true);
        
        // Set company
        JobRequestDto.CompanyId company = new JobRequestDto.CompanyId();
        company.setId(companyId);
        jobDto.setCompany(company);
        
        // Set skills
        JobRequestDto.SkillId skill = new JobRequestDto.SkillId();
        skill.setId(skillId);
        jobDto.setSkills(Arrays.asList(skill));

        MvcResult result = mockMvc.perform(post("/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobDto))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("data").get("id").asLong();
    }

    public static String getCookieValue(MvcResult result, String cookieName) {
        String cookie = result.getResponse().getCookie(cookieName).getValue();
        return cookie;
    }
}
