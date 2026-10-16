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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Characterisation of the resume API; must survive the migration unchanged. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResumeApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    final String sfx = String.valueOf(System.nanoTime());
    final String password = "Passw0rd!x";
    final String emailC1 = "cand1-" + sfx + "@test.com", emailC2 = "cand2-" + sfx + "@test.com";
    String adminToken, recA, recB, c1, c2;
    long userC1, userC2, job1, job2, jobOfB, resume1;

    @BeforeAll
    void setUp() throws Exception {
        adminToken = TestHelper.getAdminToken(mockMvc, objectMapper);
        long skill = id(call(post("/skills"), adminToken, "{\"name\":\"ResSkill-" + sfx + "\"}"));
        recA = user("resa-" + sfx + "@test.com", true);
        recB = user("resb-" + sfx + "@test.com", true);
        long companyA = company(recA, "ResCoA " + sfx), companyB = company(recB, "ResCoB " + sfx);
        job1 = id(call(post("/jobs/company"), recA, job("Dev 1 " + sfx, companyA, skill)));
        job2 = id(call(post("/jobs/company"), recA, job("Dev 2 " + sfx, companyA, skill)));
        jobOfB = id(call(post("/jobs/company"), recB, job("Dev B " + sfx, companyB, skill)));
        c1 = user(emailC1, false);
        c2 = user(emailC2, false);
        userC1 = me(c1);
        userC2 = me(c2);
    }

    // ---------------------------------------------------------------- helpers
    private String user(String email, boolean recruiter) throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"name\":\"n\",\"recruiter\":" + recruiter + "}"))
                .andExpect(status().isOk());
        return TestHelper.getUserToken(mockMvc, objectMapper, email, password);
    }

    private long me(String token) throws Exception {
        return id(call(get("/auth/me"), token, null));
    }

    private long company(String token, String name) throws Exception {
        return id(mockMvc.perform(multipart(HttpMethod.POST, "/companies/me")
                .file(new MockMultipartFile("company", "c.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"name\":\"" + name + "\",\"description\":\"d\",\"address\":\"a\"}").getBytes()))
                .header("Authorization", "Bearer " + token)).andExpect(status().isCreated()));
    }

    private String job(String name, long companyId, long skillId) {
        return "{\"name\":\"" + name + "\",\"location\":\"HCM\",\"salary\":1,\"quantity\":1,\"level\":\"FRESHER\",\"description\":\"d\","
                + "\"startDate\":\"2030-01-01T00:00:00Z\",\"endDate\":\"2030-06-01T00:00:00Z\",\"active\":true,"
                + "\"company\":{\"id\":" + companyId + "},\"skills\":[{\"id\":" + skillId + "}]}";
    }

    private ResultActions call(MockHttpServletRequestBuilder b, String token, String body) throws Exception {
        if (token != null) b.header("Authorization", "Bearer " + token);
        if (body != null) b.contentType(MediaType.APPLICATION_JSON).content(body);
        return mockMvc.perform(b);
    }

    private long id(ResultActions r) throws Exception {
        return objectMapper.readTree(r.andReturn().getResponse().getContentAsString()).get("data").get("id").asLong();
    }

    private ResultActions apply(String token, String email, long userId, long jobId, boolean withPdf) throws Exception {
        var req = multipart(HttpMethod.POST, "/resumes")
                .file(new MockMultipartFile("resume", "r.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"email\":\"" + email + "\",\"status\":\"PENDING\",\"user\":{\"id\":" + userId + "},\"job\":{\"id\":" + jobId + "}}").getBytes()));
        if (withPdf) req.file(new MockMultipartFile("pdfFile", "cv.pdf", "application/pdf", "%PDF-1.4 cv".getBytes()));
        return mockMvc.perform(req.header("Authorization", "Bearer " + token));
    }

    private ResultActions replaceFile(String token, long resumeId) throws Exception {
        return mockMvc.perform(multipart(HttpMethod.PUT, "/resumes/me/file/" + resumeId)
                .file(new MockMultipartFile("pdfFile", "cv2.pdf", "application/pdf", "%PDF-1.4 v2".getBytes()))
                .header("Authorization", "Bearer " + token));
    }

    // ---------------------------------------------------------------- tests
    @Test @Order(1) @DisplayName("apply to a job (201); duplicate -> 409; unknown job -> 404; someone else's email -> 404")
    void apply() throws Exception {
        ResultActions r = apply(c1, emailC1, userC1, job1, true)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.createdBy").value(emailC1));
        resume1 = id(r);

        apply(c1, emailC1, userC1, job1, true).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The user has already applied for this job"));
        apply(c1, emailC1, userC1, 999999, true).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Job not found"));
        apply(c1, "someone-else@test.com", userC1, job2, true).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Access denied"));
        apply(c1, emailC1, 999999, job2, true).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
        apply(c1, emailC1, userC1, job2, false).andExpect(status().is4xxClientError());
        // the failed attempts above must not have created resumes
        call(get("/resumes/me"), c1, null).andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test @Order(2) @DisplayName("candidate lists own resumes with display data")
    void listOwn() throws Exception {
        call(get("/resumes/me").param("page", "1").param("size", "5"), c1, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(resume1))
                .andExpect(jsonPath("$.data.content[0].status").value("PENDING"))
                .andExpect(jsonPath("$.data.content[0].pdfUrl", containsString("resume")))
                .andExpect(jsonPath("$.data.content[0].user.email").value(emailC1))
                .andExpect(jsonPath("$.data.content[0].job.name").value("Dev 1 " + sfx))
                .andExpect(jsonPath("$.data.content[0].job.level").value("FRESHER"))
                .andExpect(jsonPath("$.data.content[0].job.skills.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].company.name").value("ResCoA " + sfx))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(5))
                .andExpect(jsonPath("$.data.totalElements").value(1));
        call(get("/resumes/me"), c2, null).andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test @Order(3) @DisplayName("replace the file of own resume; others' / empty file -> 404")
    void replaceFile() throws Exception {
        replaceFile(c1, resume1).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(resume1))
                .andExpect(jsonPath("$.data.email").value(emailC1))
                .andExpect(jsonPath("$.data.jobName").value("Dev 1 " + sfx))
                .andExpect(jsonPath("$.data.companyName").value("ResCoA " + sfx));
        replaceFile(c2, resume1).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resume not found"));
        mockMvc.perform(multipart(HttpMethod.PUT, "/resumes/me/file/" + resume1)
                        .file(new MockMultipartFile("pdfFile", "empty.pdf", "application/pdf", new byte[0]))
                        .header("Authorization", "Bearer " + c1))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("PDF file not found"));
    }

    @Test @Order(4) @DisplayName("recruiters only see resumes sent to their own company")
    void recruiterList() throws Exception {
        call(get("/resumes/company"), recA, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[*].id", contains((int) resume1)))
                .andExpect(jsonPath("$.data.totalElements").value(1));
        call(get("/resumes/company"), recB, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
        // the suite shares one database, so ask for a page big enough to hold every resume created so far
        call(get("/resumes?size=1000"), adminToken, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[*].id", hasItem((int) resume1)));
    }

    @Test @Order(5) @DisplayName("status: recruiter of the receiving company only; admin any; 404 on unknown")
    void changeStatus() throws Exception {
        call(put("/resumes/company/status"), recB, "{\"id\":" + resume1 + ",\"status\":\"APPROVED\"}").andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));
        call(put("/resumes/company/status"), recA, "{\"id\":" + resume1 + ",\"status\":\"REVIEWING\"}").andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(resume1));
        call(get("/resumes/me"), c1, null).andExpect(jsonPath("$.data.content[0].status").value("REVIEWING"));
        call(put("/resumes/status"), adminToken, "{\"id\":" + resume1 + ",\"status\":\"APPROVED\"}").andExpect(status().isOk());
        call(get("/resumes/me"), c1, null).andExpect(jsonPath("$.data.content[0].status").value("APPROVED"));
        call(put("/resumes/status"), adminToken, "{\"id\":999999,\"status\":\"APPROVED\"}").andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resume not found"));
        call(put("/resumes/company/status"), recA, "{\"id\":999999,\"status\":\"APPROVED\"}").andExpect(status().isNotFound());
    }

    @Test @Order(6) @DisplayName("withdraw an application; second time -> 404")
    void withdraw() throws Exception {
        call(delete("/resumes/me/jobs/" + job1), c1, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(resume1))
                .andExpect(jsonPath("$.data.jobName").value("Dev 1 " + sfx));
        call(delete("/resumes/me/jobs/" + job1), c1, null).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resume not found"));
        call(get("/resumes/me"), c1, null).andExpect(jsonPath("$.data.content").isEmpty());
        // can apply again afterwards
        apply(c1, emailC1, userC1, job1, true).andExpect(status().isCreated());
    }

    @Test @Order(7) @DisplayName("deleting a job removes the resumes sent to it")
    void jobDeletionRemovesResumes() throws Exception {
        apply(c2, emailC2, userC2, job2, true).andExpect(status().isCreated());
        call(get("/resumes/me"), c2, null).andExpect(jsonPath("$.data.totalElements").value(1));
        call(delete("/jobs/company/" + job2), recA, null).andExpect(status().isOk());
        call(get("/resumes/me"), c2, null).andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test @Order(8) @DisplayName("anonymous -> 401; candidate cannot list the company's resumes -> 403")
    void security() throws Exception {
        call(get("/resumes/me"), null, null).andExpect(status().isUnauthorized());
        call(get("/resumes/company"), c1, null).andExpect(status().isForbidden());
        call(put("/resumes/status"), c1, "{\"id\":1,\"status\":\"APPROVED\"}").andExpect(status().isForbidden());
    }
}
