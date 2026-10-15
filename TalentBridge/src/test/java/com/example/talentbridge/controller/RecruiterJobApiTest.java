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

/** Characterisation of the job API as used by recruiters and admins; must survive the migration unchanged. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecruiterJobApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    final String sfx = String.valueOf(System.nanoTime());
    final String password = "Passw0rd!x";
    String adminToken, tokenA, tokenB, tokenNoCompany;
    long companyA, companyB, skill1, skill2, jobA, jobB;

    @BeforeAll
    void setUp() throws Exception {
        adminToken = TestHelper.getAdminToken(mockMvc, objectMapper);
        skill1 = skill("JobSkill1-" + sfx);
        skill2 = skill("JobSkill2-" + sfx);
        tokenA = recruiter("job-a-" + sfx + "@test.com");
        tokenB = recruiter("job-b-" + sfx + "@test.com");
        tokenNoCompany = recruiter("job-n-" + sfx + "@test.com");
        companyA = company(tokenA, "JobCoA " + sfx);
        companyB = company(tokenB, "JobCoB " + sfx);
    }

    private long skill(String name) throws Exception {
        String res = mockMvc.perform(post("/skills").header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(res).get("data").get("id").asLong();
    }

    private String recruiter(String email) throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"name\":\"r\",\"recruiter\":true}"))
                .andExpect(status().isOk());
        return TestHelper.getUserToken(mockMvc, objectMapper, email, password);
    }

    private long company(String token, String name) throws Exception {
        String res = mockMvc.perform(multipart(HttpMethod.POST, "/companies/me")
                .file(new MockMultipartFile("company", "c.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"name\":\"" + name + "\",\"description\":\"d\",\"address\":\"addr\"}").getBytes()))
                .header("Authorization", "Bearer " + token)).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(res).get("data").get("id").asLong();
    }

    private String job(String name, Long companyId, boolean active, long... skillIds) {
        StringBuilder sk = new StringBuilder();
        for (int i = 0; i < skillIds.length; i++) sk.append(i > 0 ? "," : "").append("{\"id\":").append(skillIds[i]).append("}");
        return "{\"name\":\"" + name + "\",\"location\":\"HCM\",\"salary\":1500,\"quantity\":2,\"level\":\"SENIOR\",\"description\":\"desc\","
                + "\"startDate\":\"2030-01-01T00:00:00Z\",\"endDate\":\"2030-06-01T00:00:00Z\",\"active\":" + active
                + ",\"company\":{\"id\":" + companyId + "},\"skills\":[" + sk + "]}";
    }

    private ResultActions call(MockHttpServletRequestBuilder b, String token, String body) throws Exception {
        if (token != null) b.header("Authorization", "Bearer " + token);
        if (body != null) b.contentType(MediaType.APPLICATION_JSON).content(body);
        return mockMvc.perform(b);
    }

    private long id(ResultActions r) throws Exception {
        return objectMapper.readTree(r.andReturn().getResponse().getContentAsString()).get("data").get("id").asLong();
    }

    @Test @Order(1) @DisplayName("recruiter creates a job for their own company (company from the account, not the body)")
    void recruiterCreates() throws Exception {
        ResultActions r = call(post("/jobs/company"), tokenA, job("Backend A " + sfx, companyB, true, skill1, skill2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Backend A " + sfx))
                .andExpect(jsonPath("$.data.level").value("SENIOR"))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.data.company.id").value(companyA))
                .andExpect(jsonPath("$.data.company.name").value("JobCoA " + sfx))
                .andExpect(jsonPath("$.data.company.address").value("addr"))
                .andExpect(jsonPath("$.data.skills[*].id", containsInAnyOrder((int) skill1, (int) skill2)))
                .andExpect(jsonPath("$.data.skills[*].name", hasItem("JobSkill1-" + sfx)));
        jobA = id(r);
        jobB = id(call(post("/jobs/company"), tokenB, job("Backend B " + sfx, companyB, true, skill1)).andExpect(status().isOk()));
    }

    @Test @Order(2) @DisplayName("create: unknown skill -> 404, recruiter without company -> 404, validation -> 400")
    void createErrors() throws Exception {
        call(post("/jobs/company"), tokenA, job("x", companyA, true, skill1, 999999)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("ENTITY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Kỹ năng không tồn tại"));
        call(post("/jobs/company"), tokenNoCompany, job("x", companyA, true, skill1)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Không tìm thấy công ty người dùng"));
        call(post("/jobs/company"), tokenA, job("x", companyA, true, skill1).replace("\"salary\":1500", "\"salary\":-1"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test @Order(3) @DisplayName("recruiter lists only their company's jobs (paging + filter)")
    void recruiterList() throws Exception {
        call(get("/jobs/company").param("page", "1").param("size", "5"), tokenA, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(jobA))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(5))
                .andExpect(jsonPath("$.data.totalElements").value(1));
        call(get("/jobs/company").param("filter", "name~'*nomatch*'"), tokenA, null).andExpect(jsonPath("$.data.content").isEmpty());
        call(get("/jobs/company"), tokenNoCompany, null).andExpect(status().isNotFound());
    }

    @Test @Order(4) @DisplayName("public reads: by id, by company (without description/company), filtered list")
    void publicReads() throws Exception {
        call(get("/jobs/" + jobA), null, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(jobA)).andExpect(jsonPath("$.data.description").value("desc"));
        call(get("/jobs/999999"), null, null).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Không tìm thấy công việc"));
        call(get("/jobs/companies/" + companyA), null, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(jobA))
                .andExpect(jsonPath("$.data[0].description").doesNotExist())
                .andExpect(jsonPath("$.data[0].company").doesNotExist())
                .andExpect(jsonPath("$.data[0].skills.length()").value(2));
        call(get("/jobs").param("filter", "skills.id:" + skill2), null, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[*].id", contains((int) jobA)));
        call(get("/jobs").param("filter", "company.id:" + companyB), null, null)
                .andExpect(jsonPath("$.data.content[*].id", contains((int) jobB)));
    }

    @Test @Order(5) @DisplayName("recruiter updates own job (skills replaced); another company's job -> 403")
    void recruiterUpdates() throws Exception {
        call(put("/jobs/company/" + jobA), tokenA, job("Backend A2 " + sfx, companyA, false, skill2)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Backend A2 " + sfx))
                .andExpect(jsonPath("$.data.active").value(false))
                .andExpect(jsonPath("$.data.skills.length()").value(1))
                .andExpect(jsonPath("$.data.skills[0].id").value(skill2));
        call(put("/jobs/company/" + jobB), tokenA, job("hack", companyA, true, skill1)).andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));
        call(put("/jobs/company/999999"), tokenA, job("x", companyA, true, skill1)).andExpect(status().isNotFound());
        call(get("/jobs/" + jobB), null, null).andExpect(jsonPath("$.data.name").value("Backend B " + sfx));
    }

    @Test @Order(6) @DisplayName("admin updates any job, can move it to another company; unknown company -> 404; unknown skills ignored")
    void adminUpdates() throws Exception {
        call(put("/jobs/" + jobA), adminToken, job("Moved " + sfx, companyB, true, skill1, 999999)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.company.id").value(companyB))
                .andExpect(jsonPath("$.data.skills[*].id", contains((int) skill1)));
        call(put("/jobs/" + jobA), adminToken, job("x", 999999L, true, skill1)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Công ty không tồn tại"));
    }

    @Test @Order(7) @DisplayName("admin creates a job for any company; unknown company -> 404")
    void adminCreates() throws Exception {
        long j = id(call(post("/jobs"), adminToken, job("Admin made " + sfx, companyA, true, skill1)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.company.id").value(companyA)));
        call(post("/jobs"), adminToken, job("x", 999999L, true, skill1)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Công ty không tồn tại"));
        call(delete("/jobs/" + j), adminToken, null).andExpect(status().isOk());
    }

    @Test @Order(8) @DisplayName("deleting: recruiter only own (403 otherwise), admin any; second delete -> 404")
    void deleting() throws Exception {
        call(delete("/jobs/company/" + jobB), tokenA, null).andExpect(status().isForbidden());
        call(delete("/jobs/company/" + jobB), tokenB, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(jobB));
        call(get("/jobs/" + jobB), null, null).andExpect(status().isNotFound());
        call(delete("/jobs/company/" + jobB), tokenB, null).andExpect(status().isNotFound());

        call(delete("/jobs/" + jobA), adminToken, null).andExpect(status().isOk());
        call(get("/jobs/" + jobA), null, null).andExpect(status().isNotFound());
        call(delete("/jobs/" + jobA), adminToken, null).andExpect(status().isNotFound());
        // skills survive the deletion of the jobs that used them
        call(get("/skills/" + skill1), adminToken, null).andExpect(status().isOk());
    }
}
