package com.example.talentbridge.matching;

import com.example.talentbridge.controller.TestHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(FakeMatcherConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchingApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    final String sfx = String.valueOf(System.nanoTime());
    String admin, recruiter, rival, strong, weak, unscored, boom;
    long javaSkill, jobId, strongResume, weakResume, unscoredResume, boomResume;
    String strongMail;

    private String register(String email, boolean recruiter) throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"Passw0rd!x\",\"name\":\"n\",\"recruiter\":" + recruiter + "}"))
                .andExpect(status().isOk());
        return email;
    }

    private String token(String email) throws Exception {
        return TestHelper.getUserToken(mockMvc, objectMapper, email, "Passw0rd!x");
    }

    private ResultActions call(MockHttpServletRequestBuilder b, String token, String body) throws Exception {
        if (token != null) b.header("Authorization", "Bearer " + token);
        if (body != null) b.contentType(MediaType.APPLICATION_JSON).content(body);
        return mockMvc.perform(b);
    }

    private JsonNode data(ResultActions r) throws Exception {
        return objectMapper.readTree(r.andReturn().getResponse().getContentAsString()).get("data");
    }

    private long id(ResultActions r) throws Exception {
        return data(r).get("id").asLong();
    }

    private long apply(String candidateMail, String candidateToken, String cv) throws Exception {
        long candId = id(call(get("/auth/me"), candidateToken, null));
        return id(mockMvc.perform(multipart(HttpMethod.POST, "/resumes")
                .file(new MockMultipartFile("resume", "r.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"email\":\"" + candidateMail + "\",\"status\":\"PENDING\",\"user\":{\"id\":" + candId + "},\"job\":{\"id\":" + jobId + "}}").getBytes()))
                .file(new MockMultipartFile("pdfFile", "cv.pdf", "application/pdf", cv.getBytes()))
                .header("Authorization", "Bearer " + candidateToken)).andExpect(status().isCreated()));
    }

    private String company(String recruiterMail, String name) throws Exception {
        String t = token(recruiterMail);
        mockMvc.perform(multipart(HttpMethod.POST, "/companies/me")
                .file(new MockMultipartFile("company", "c.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"name\":\"" + name + "\",\"description\":\"d\",\"address\":\"a\"}").getBytes()))
                .header("Authorization", "Bearer " + t)).andExpect(status().isCreated());
        return token(recruiterMail);
    }

    @BeforeAll
    void setUp() throws Exception {
        admin = TestHelper.getAdminToken(mockMvc, objectMapper);
        javaSkill = id(call(post("/skills"), admin, "{\"name\":\"MJava-" + sfx + "\"}"));
        long sql = id(call(post("/skills"), admin, "{\"name\":\"MSql-" + sfx + "\"}"));
        recruiter = company(register("mrec-" + sfx + "@test.com", true), "MatchCo " + sfx);
        rival = company(register("mrival-" + sfx + "@test.com", true), "RivalCo " + sfx);
        long companyId = id(call(get("/companies/me"), recruiter, null));
        jobId = id(call(post("/jobs/company"), recruiter, "{\"name\":\"Match Dev " + sfx + "\",\"location\":\"HCM\",\"salary\":1,\"quantity\":1,"
                + "\"level\":\"FRESHER\",\"description\":\"APIs\",\"startDate\":\"2030-01-01T00:00:00Z\",\"endDate\":\"2030-06-01T00:00:00Z\","
                + "\"active\":true,\"company\":{\"id\":" + companyId + "},\"skills\":[{\"id\":" + javaSkill + "},{\"id\":" + sql + "}]}"));

        strongMail = register("mstrong-" + sfx + "@test.com", false);
        strong = token(strongMail);
        String weakMail = register("mweak-" + sfx + "@test.com", false);
        weak = token(weakMail);
        String boomMail = register("mboom-" + sfx + "@test.com", false);
        boom = token(boomMail);
        String unscoredMail = register("munscored-" + sfx + "@test.com", false);
        unscored = token(unscoredMail);

        weakResume = apply(weakMail, weak, "%PDF cv score=35");
        strongResume = apply(strongMail, strong, "%PDF cv score=88");
        boomResume = apply(boomMail, boom, "%PDF boom");
        unscoredResume = apply(unscoredMail, unscored, "%PDF no marker");   // fake answers 50
    }

    @Test @Order(1) @DisplayName("an application is scored when it is submitted; a failing judge leaves it unscored without failing the submission")
    void scoredOnSubmit() throws Exception {
        call(get("/matching/resumes/" + strongResume), recruiter, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.scored").value(true))
                .andExpect(jsonPath("$.data.match.score").value(88))
                .andExpect(jsonPath("$.data.match.model").value("fake-model"))
                .andExpect(jsonPath("$.data.match.matchedSkills.length()").value(1))
                .andExpect(jsonPath("$.data.match.missingSkills.length()").value(1))
                .andExpect(jsonPath("$.data.match.summary").value("Fake summary for Match Dev " + sfx));
        call(get("/matching/resumes/" + boomResume), recruiter, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.scored").value(false))
                .andExpect(jsonPath("$.data.match").doesNotExist());
    }

    @Test @Order(2) @DisplayName("applicants are ranked by score, unscored last")
    void ranking() throws Exception {
        JsonNode ranked = data(call(get("/matching/jobs/" + jobId + "/candidates"), recruiter, null).andExpect(status().isOk()));
        assertEquals(4, ranked.size());
        assertEquals(strongResume, ranked.get(0).get("resumeId").asLong());
        assertEquals(88, ranked.get(0).get("match").get("score").asInt());
        assertEquals(unscoredResume, ranked.get(1).get("resumeId").asLong());
        assertEquals(weakResume, ranked.get(2).get("resumeId").asLong());
        assertEquals(boomResume, ranked.get(3).get("resumeId").asLong());
        org.junit.jupiter.api.Assertions.assertTrue(ranked.get(3).get("match").isNull());
        assertEquals(strongMail, ranked.get(0).get("email").asText());
    }

    @Test @Order(3) @DisplayName("only the company's own people (or an admin) see matches; candidates and rivals are refused")
    void access() throws Exception {
        call(get("/matching/resumes/" + strongResume), null, null).andExpect(status().isUnauthorized());
        call(get("/matching/resumes/" + strongResume), strong, null).andExpect(status().isForbidden());
        call(get("/matching/resumes/" + strongResume), rival, null).andExpect(status().isForbidden());
        call(get("/matching/jobs/" + jobId + "/candidates"), rival, null).andExpect(status().isForbidden());
        call(get("/matching/jobs/" + jobId + "/candidates"), strong, null).andExpect(status().isForbidden());
        call(post("/matching/resumes/" + strongResume + "/recompute"), rival, null).andExpect(status().isForbidden());
        call(get("/matching/resumes/999999"), recruiter, null).andExpect(status().isNotFound());
        call(get("/matching/jobs/999999/candidates"), recruiter, null).andExpect(status().isNotFound());
        call(get("/matching/resumes/" + strongResume), admin, null).andExpect(status().isOk());
        call(get("/matching/jobs/" + jobId + "/candidates"), admin, null).andExpect(status().isOk());
    }

    @Test @Order(4) @DisplayName("recompute is accepted and replaces the stored result")
    void recompute() throws Exception {
        int before = FakeMatcherConfig.CALLS.get();
        call(post("/matching/resumes/" + boomResume + "/recompute"), recruiter, null).andExpect(status().isAccepted());
        org.junit.jupiter.api.Assertions.assertTrue(FakeMatcherConfig.CALLS.get() > before);
        call(post("/matching/resumes/" + strongResume + "/recompute"), recruiter, null).andExpect(status().isAccepted());
        call(get("/matching/resumes/" + strongResume), recruiter, null)
                .andExpect(jsonPath("$.data.match.score").value(88));
    }

    @Test @Order(5) @DisplayName("status tells recruiters the feature is on; candidates are not allowed to ask")
    void featureStatus() throws Exception {
        call(get("/matching/status"), recruiter, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enabled").value(true))
                .andExpect(jsonPath("$.data.model").value("fake-model"));
        call(get("/matching/status"), strong, null).andExpect(status().isForbidden());
    }

    @Test @Order(6) @DisplayName("a candidate gets jobs ranked by how many required skills they follow")
    void recommendations() throws Exception {
        call(get("/matching/me/recommendations"), strong, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));       // follows nothing yet
        call(get("/matching/me/recommendations"), recruiter, null).andExpect(status().isForbidden());

        call(post("/subscribers/me"), strong, "{\"skills\":[{\"id\":" + javaSkill + "}]}").andExpect(status().is2xxSuccessful());

        JsonNode recs = data(call(get("/matching/me/recommendations"), strong, null).andExpect(status().isOk()));
        assertEquals(1, recs.size());
        assertEquals(jobId, recs.get(0).get("jobId").asLong());
        assertEquals(50, recs.get(0).get("fitPercent").asInt());          // 1 of the job's 2 skills
        assertEquals(1, recs.get(0).get("sharedSkills").size());
    }
}
