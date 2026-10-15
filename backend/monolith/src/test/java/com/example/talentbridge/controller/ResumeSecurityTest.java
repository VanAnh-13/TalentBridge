package com.example.talentbridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Applying for a job must be done as yourself and cannot pre-decide the outcome.
 * These tests failed on the original implementation (see commit message of the resume migration).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResumeSecurityTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    final String sfx = String.valueOf(System.nanoTime());
    final String password = "Passw0rd!x";
    final String victimEmail = "victim-" + sfx + "@test.com", attackerEmail = "attacker-" + sfx + "@test.com";
    String adminToken, recruiter, victim, attacker;
    long victimId, jobId;

    @BeforeAll
    void setUp() throws Exception {
        adminToken = TestHelper.getAdminToken(mockMvc, objectMapper);
        long skill = id(call(post("/skills"), adminToken, "{\"name\":\"SecSkill-" + sfx + "\"}"));
        recruiter = register("secrec-" + sfx + "@test.com", true);
        long company = id(mockMvc.perform(multipart(HttpMethod.POST, "/companies/me")
                .file(new MockMultipartFile("company", "c.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"name\":\"SecCo " + sfx + "\",\"description\":\"d\",\"address\":\"a\"}").getBytes()))
                .header("Authorization", "Bearer " + recruiter)).andExpect(status().isCreated()));
        jobId = id(call(post("/jobs/company"), recruiter,
                "{\"name\":\"Sec job\",\"location\":\"l\",\"salary\":1,\"quantity\":1,\"level\":\"FRESHER\",\"description\":\"d\","
                        + "\"startDate\":\"2030-01-01T00:00:00Z\",\"endDate\":\"2030-06-01T00:00:00Z\",\"active\":true,"
                        + "\"company\":{\"id\":" + company + "},\"skills\":[{\"id\":" + skill + "}]}"));
        victim = register(victimEmail, false);
        attacker = register(attackerEmail, false);
        victimId = id(call(get("/auth/me"), victim, null));
    }

    private String register(String email, boolean recruiter) throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"name\":\"n\",\"recruiter\":" + recruiter + "}"))
                .andExpect(status().isOk());
        return TestHelper.getUserToken(mockMvc, objectMapper, email, password);
    }

    private ResultActions call(MockHttpServletRequestBuilder b, String token, String body) throws Exception {
        b.header("Authorization", "Bearer " + token);
        if (body != null) b.contentType(MediaType.APPLICATION_JSON).content(body);
        return mockMvc.perform(b);
    }

    private long id(ResultActions r) throws Exception {
        return objectMapper.readTree(r.andReturn().getResponse().getContentAsString()).get("data").get("id").asLong();
    }

    private ResultActions submit(String token, String json) throws Exception {
        return mockMvc.perform(multipart(HttpMethod.POST, "/resumes")
                .file(new MockMultipartFile("resume", "r.json", MediaType.APPLICATION_JSON_VALUE, json.getBytes()))
                .file(new MockMultipartFile("pdfFile", "cv.pdf", "application/pdf", "%PDF-1.4".getBytes()))
                .header("Authorization", "Bearer " + token));
    }

    @Test
    @DisplayName("an attacker cannot submit a resume in somebody else's name (needs only their id and email)")
    void cannotApplyAsSomeoneElse() throws Exception {
        submit(attacker, "{\"email\":\"" + victimEmail + "\",\"status\":\"PENDING\",\"user\":{\"id\":" + victimId + "},\"job\":{\"id\":" + jobId + "}}")
                .andExpect(status().isForbidden());
        call(get("/resumes/me"), victim, null).andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test
    @DisplayName("a candidate cannot choose the status of their own application: it always starts as PENDING")
    void statusAlwaysStartsPending() throws Exception {
        long attackerId = id(call(get("/auth/me"), attacker, null));
        submit(attacker, "{\"email\":\"" + attackerEmail + "\",\"status\":\"APPROVED\",\"user\":{\"id\":" + attackerId + "},\"job\":{\"id\":" + jobId + "}}")
                .andExpect(status().isCreated());
        call(get("/resumes/me"), attacker, null).andExpect(jsonPath("$.data.content[0].status").value("PENDING"));
    }
}
