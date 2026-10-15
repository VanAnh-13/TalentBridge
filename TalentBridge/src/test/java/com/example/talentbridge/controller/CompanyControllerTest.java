package com.example.talentbridge.controller;

import com.example.talentbridge.dto.request.company.DefaultCompanyRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.example.talentbridge.skill.infrastructure.persistence.SkillJpaRepository skillRepository;

    @Autowired
    private com.example.talentbridge.repository.JobRepository jobRepository;

    private String adminToken;
    private Long companyId;
    private String companyName = "Test Company " + System.currentTimeMillis();

    @BeforeAll
    void setUp() throws Exception {
        // Login as admin to get token
        adminToken = TestHelper.getAdminToken(mockMvc, objectMapper);
    }

    @Test
    @Order(1)
    @DisplayName("COMPANY_01: Tạo công ty mới với đầy đủ thông tin")
    void testCreateCompanyWithFullInfo() throws Exception {
        DefaultCompanyRequestDto companyDto = new DefaultCompanyRequestDto();
        companyDto.setName(companyName);
        companyDto.setAddress("123 Test Street, Test City");
        companyDto.setDescription("A test company for automation testing");

        MockMultipartFile companyData = new MockMultipartFile(
            "company",
            "company.json",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(companyDto)
        );

        MockMultipartFile logoFile = new MockMultipartFile(
            "logoFile",
            "logo.png",
            MediaType.IMAGE_PNG_VALUE,
            "fake logo content".getBytes()
        );

        MvcResult result = mockMvc.perform(multipart("/companies")
                .file(companyData)
                .file(logoFile)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value(companyName))
                .andExpect(jsonPath("$.data.address").value("123 Test Street, Test City"))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        companyId = objectMapper.readTree(response).get("data").get("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("COMPANY_02: Tạo công ty với tên trùng lặp")
    void testCreateCompanyWithDuplicateName() throws Exception {
        DefaultCompanyRequestDto companyDto = new DefaultCompanyRequestDto();
        companyDto.setName(companyName.toUpperCase()); // same name, different case
        companyDto.setDescription("Another description");
        companyDto.setAddress("456 Another Street");

        MockMultipartFile companyData = new MockMultipartFile(
            "company",
            "company.json",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(companyDto)
        );

        mockMvc.perform(multipart("/companies")
                .file(companyData)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("đã tồn tại")));
    }

    @Test
    @Order(3)
    @DisplayName("COMPANY_03: Lấy danh sách công ty với filter")
    void testGetCompaniesWithFilter() throws Exception {
        mockMvc.perform(get("/companies")
                .param("filter", "name~'*Test*'")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[?(@.name == '" + companyName + "')]").exists());
    }

    @Test
    @Order(4)
    @DisplayName("COMPANY_04: Lấy chi tiết công ty theo ID")
    void testGetCompanyById() throws Exception {
        mockMvc.perform(get("/companies/" + companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(companyId))
                .andExpect(jsonPath("$.data.name").value(companyName));
    }

    @Test
    @Order(5)
    @DisplayName("COMPANY_05: Cập nhật thông tin công ty")
    void testUpdateCompany() throws Exception {
        DefaultCompanyRequestDto updateDto = new DefaultCompanyRequestDto();
        updateDto.setName(companyName + " Updated");
        updateDto.setAddress("789 Updated Street");
        updateDto.setDescription("Updated description");

        MockMultipartFile companyData = new MockMultipartFile(
            "company",
            "company.json",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(updateDto)
        );

        mockMvc.perform(multipart("/companies/" + companyId)
                .file(companyData)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(companyName + " Updated"))
                .andExpect(jsonPath("$.data.address").value("789 Updated Street"));
    }

    @Test
    @Order(6)
    @DisplayName("COMPANY_10: Lấy danh sách công ty kèm số lượng job")
    void testGetCompaniesWithJobCount() throws Exception {
        mockMvc.perform(get("/companies/with-jobs-count")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].jobsCount").isNumber());
    }

    @Test
    @Order(7)
    @DisplayName("COMPANY_09: Xóa công ty còn job đang tuyển -> bị từ chối (expect fail)")
    void testDeleteCompanyWithActiveJobs() throws Exception {
        com.example.talentbridge.skill.infrastructure.persistence.SkillJpaEntity skill = new com.example.talentbridge.skill.infrastructure.persistence.SkillJpaEntity();
        skill.setName("skill-" + System.nanoTime());
        Long skillId = skillRepository.save(skill).getId();
        Long jobId = TestHelper.createJobForCompany(mockMvc, objectMapper, adminToken, companyId, skillId); // active = true

        mockMvc.perform(delete("/companies/" + companyId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("BUSINESS_RULE_VIOLATION"))
                .andExpect(jsonPath("$.message", containsString("has active jobs")));

        // nothing was deleted by the rejected request
        mockMvc.perform(get("/companies/" + companyId)).andExpect(status().isOk());
        mockMvc.perform(get("/jobs/" + jobId)).andExpect(status().isOk());

        // close the job: now the company can be deleted (its closed jobs go with it)
        jobRepository.findById(jobId).ifPresent(j -> {
            j.setActive(false);
            jobRepository.save(j);
        });

        mockMvc.perform(delete("/companies/" + companyId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        mockMvc.perform(get("/jobs/" + jobId)).andExpect(status().isNotFound());
    }
}
