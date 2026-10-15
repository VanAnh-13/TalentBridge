package com.example.talentbridge.controller;

import com.example.talentbridge.dto.request.job.JobRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.example.talentbridge.repository.CompanyRepository companyRepository;

    @Autowired
    private com.example.talentbridge.repository.SkillRepository skillRepository;

    private Long companyId;
    private Long skillId;

    private String adminToken;
    private Long jobId;

    @BeforeAll
    void setUp() throws Exception {
        adminToken = TestHelper.getAdminToken(mockMvc, objectMapper);
        companyId = companyRepository.save(
                new com.example.talentbridge.model.Company("Job Test Co " + System.nanoTime(), "desc", "addr")).getId();
        com.example.talentbridge.model.Skill skill = new com.example.talentbridge.model.Skill();
        skill.setName("job-test-skill-" + System.nanoTime());
        skillId = skillRepository.save(skill).getId();
    }

    @Test
    @Order(1)
    @DisplayName("JOB_01: Tạo job mới với thông tin hợp lệ")
    void testCreateJobWithValidData() throws Exception {
        JobRequestDto jobDto = new JobRequestDto();
        jobDto.setName("Software Engineer Position");
        jobDto.setLocation("Ho Chi Minh City");
        jobDto.setDescription("We are looking for a talented software engineer");
        jobDto.setSalary(1500.0);
        jobDto.setQuantity(3);
        jobDto.setLevel(com.example.talentbridge.model.constant.Level.MIDDLE);
        jobDto.setStartDate(Instant.now());
        jobDto.setEndDate(Instant.now().plus(60, ChronoUnit.DAYS));
        jobDto.setActive(true);
        
        // Assuming company with ID 1 exists
        JobRequestDto.CompanyId company = new JobRequestDto.CompanyId();
        company.setId(companyId);
        jobDto.setCompany(company);
        
        // Assuming skill with ID 1 exists
        JobRequestDto.SkillId skill = new JobRequestDto.SkillId();
        skill.setId(skillId);
        jobDto.setSkills(Arrays.asList(skill));

        String response = mockMvc.perform(post("/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobDto))
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Software Engineer Position"))
                .andExpect(jsonPath("$.data.location").value("Ho Chi Minh City"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        jobId = objectMapper.readTree(response).get("data").get("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("JOB_02: Tạo job với salary âm (expect fail)")
    void testCreateJobWithNegativeSalary() throws Exception {
        JobRequestDto jobDto = new JobRequestDto();
        jobDto.setName("Invalid Job");
        jobDto.setLocation("Test Location");
        jobDto.setDescription("Test Description");
        jobDto.setSalary(-1000.0);
        jobDto.setQuantity(1);
        jobDto.setLevel(com.example.talentbridge.model.constant.Level.INTERN);
        jobDto.setStartDate(Instant.now());
        jobDto.setEndDate(Instant.now().plus(30, ChronoUnit.DAYS));
        jobDto.setActive(true);
        
        JobRequestDto.CompanyId company = new JobRequestDto.CompanyId();
        company.setId(companyId);
        jobDto.setCompany(company);
        
        JobRequestDto.SkillId skill = new JobRequestDto.SkillId();
        skill.setId(skillId);
        jobDto.setSkills(Arrays.asList(skill));

        mockMvc.perform(post("/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobDto))
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("phải lớn hơn hoặc bằng 0")));
    }

    @Test
    @Order(3)
    @DisplayName("JOB_03: Lấy danh sách job với filter theo level")
    void testGetJobsWithLevelFilter() throws Exception {
        mockMvc.perform(get("/jobs")
                .param("filter", "level:'MIDDLE'")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].level").value("MIDDLE"));
    }

    @Test
    @Order(4)
    @DisplayName("JOB_04: Lấy danh sách job theo công ty")
    void testGetJobsByCompany() throws Exception {
        mockMvc.perform(get("/jobs/companies/" + companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(5)
    @DisplayName("JOB_05: Cập nhật thông tin job")
    void testUpdateJob() throws Exception {
        JobRequestDto updateDto = new JobRequestDto();
        updateDto.setName("Updated Software Engineer Position");
        updateDto.setLocation("Ha Noi");
        updateDto.setDescription("Updated description");
        updateDto.setSalary(2000.0);
        updateDto.setQuantity(5);
        updateDto.setLevel(com.example.talentbridge.model.constant.Level.SENIOR);
        updateDto.setStartDate(Instant.now());
        updateDto.setEndDate(Instant.now().plus(90, ChronoUnit.DAYS));
        updateDto.setActive(true);
        
        JobRequestDto.CompanyId company = new JobRequestDto.CompanyId();
        company.setId(companyId);
        updateDto.setCompany(company);
        
        JobRequestDto.SkillId skill = new JobRequestDto.SkillId();
        skill.setId(skillId);
        updateDto.setSkills(Arrays.asList(skill));

        mockMvc.perform(put("/jobs/" + jobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Software Engineer Position"))
                .andExpect(jsonPath("$.data.salary").value(2000.0));
    }

    @Test
    @Order(6)
    @DisplayName("JOB_06: Đóng/mở job tuyển dụng")
    void testToggleJobStatus() throws Exception {
        // First, deactivate the job
        JobRequestDto updateDto = new JobRequestDto();
        updateDto.setName("Software Engineer Position");
        updateDto.setLocation("Ho Chi Minh City");
        updateDto.setDescription("Test Description");
        updateDto.setSalary(1500.0);
        updateDto.setQuantity(3);
        updateDto.setLevel(com.example.talentbridge.model.constant.Level.MIDDLE);
        updateDto.setStartDate(Instant.now());
        updateDto.setEndDate(Instant.now().plus(60, ChronoUnit.DAYS));
        updateDto.setActive(false); // Deactivate
        
        JobRequestDto.CompanyId company = new JobRequestDto.CompanyId();
        company.setId(companyId);
        updateDto.setCompany(company);
        
        JobRequestDto.SkillId skill = new JobRequestDto.SkillId();
        skill.setId(skillId);
        updateDto.setSkills(Arrays.asList(skill));

        mockMvc.perform(put("/jobs/" + jobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.active").value(false));
    }

    @Test
    @Order(7)
    @DisplayName("JOB_09: Tìm kiếm job theo skills")
    void testSearchJobsBySkills() throws Exception {
        mockMvc.perform(get("/jobs")
                .param("filter", "skills.id:" + skillId)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @Order(8)
    @DisplayName("JOB_10: Lấy job với phân trang và sorting")
    void testGetJobsWithPaginationAndSorting() throws Exception {
        mockMvc.perform(get("/jobs")
                .param("page", "0")
                .param("size", "5")
                .param("sort", "salary,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(5));
    }
}
