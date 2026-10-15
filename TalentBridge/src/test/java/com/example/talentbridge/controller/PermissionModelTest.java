package com.example.talentbridge.controller;

import com.example.talentbridge.model.*;
import com.example.talentbridge.model.constant.Gender;
import com.example.talentbridge.model.constant.Level;
import com.example.talentbridge.model.constant.ResumeStatus;
import com.example.talentbridge.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PermissionModelTest {

    private static final String PASSWORD = "Passw0rd!x";

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired ApplicationContext context;
    @Autowired JdbcTemplate jdbc;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired CompanyRepository companyRepository;
    @Autowired JobRepository jobRepository;
    @Autowired ResumeRepository resumeRepository;

    String adminToken, recruiterAToken, recruiterBToken, candidateToken, otherCandidateToken;
    @Autowired SkillRepository skillRepository;

    Long jobOfA, jobOfB, resumeOfCandidate, companyAId, skillId;

    @BeforeAll
    void setUp() throws Exception {
        adminToken = TestHelper.getAdminToken(mockMvc, objectMapper);

        Company companyA = companyRepository.save(new Company("PM Co A " + System.nanoTime(), "d", "a"));
        Company companyB = companyRepository.save(new Company("PM Co B " + System.nanoTime(), "d", "a"));
        companyAId = companyA.getId();
        Skill skill = new Skill();
        skill.setName("pm-skill-" + System.nanoTime());
        skillId = skillRepository.save(skill).getId();
        User recruiterA = user("recruiter-a@pm.test", "RECRUITER", companyA);
        User recruiterB = user("recruiter-b@pm.test", "RECRUITER", companyB);
        User candidate = user("candidate@pm.test", "USER", null);
        user("other-candidate@pm.test", "USER", null);

        jobOfA = job(companyA).getId();
        Job b = job(companyB);
        jobOfB = b.getId();

        Resume resume = new Resume(candidate.getEmail(), ResumeStatus.PENDING, 1L);
        resume.setUser(candidate);
        resume.setJob(b);                       // submitted to company B
        resume.setFileKey("resumes/some-key.pdf");
        resumeOfCandidate = resumeRepository.save(resume).getId();

        recruiterAToken = TestHelper.getUserToken(mockMvc, objectMapper, recruiterA.getEmail(), PASSWORD);
        recruiterBToken = TestHelper.getUserToken(mockMvc, objectMapper, recruiterB.getEmail(), PASSWORD);
        candidateToken = TestHelper.getUserToken(mockMvc, objectMapper, "candidate@pm.test", PASSWORD);
        otherCandidateToken = TestHelper.getUserToken(mockMvc, objectMapper, "other-candidate@pm.test", PASSWORD);
    }

    private User user(String email, String role, Company company) {
        User u = new User();
        u.setEmail(email);
        u.setName(email);
        u.setPassword(passwordEncoder.encode(PASSWORD));
        u.setDob(LocalDate.of(1995, 1, 1));
        u.setAddress("x");
        u.setGender(Gender.OTHER);
        u.setRole(roleRepository.findByName(role).orElseThrow());
        u.setCompany(company);
        return userRepository.save(u);
    }

    private Job job(Company company) {
        Job j = new Job("Dev", "HCM", 1000.0, 1, Level.FRESHER, "d", Instant.now(), Instant.now().plusSeconds(86400), true);
        j.setCompany(company);
        return jobRepository.save(j);
    }

    private String jobJson() {
        return "{\"name\":\"n\",\"location\":\"l\",\"salary\":1,\"quantity\":1,\"level\":\"FRESHER\",\"description\":\"d\","
                + "\"startDate\":\"2030-01-01T00:00:00Z\",\"endDate\":\"2030-02-01T00:00:00Z\",\"active\":true,"
                + "\"company\":{\"id\":" + companyAId + "},\"skills\":[{\"id\":" + skillId + "}]}";
    }

    /** multipart request with a valid "company" part (company endpoints take multipart/form-data). */
    private int callCompany(org.springframework.http.HttpMethod method, String url, String token) throws Exception {
        var part = new org.springframework.mock.web.MockMultipartFile("company", "c.json", MediaType.APPLICATION_JSON_VALUE,
                "{\"name\":\"x\",\"description\":\"d\",\"address\":\"a\"}".getBytes());
        return mockMvc.perform(multipart(method, url).file(part).header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getStatus();
    }

    private int call(String method, String url, String token) throws Exception {
        return call(method, url, token, "{}");
    }

    private int call(String method, String url, String token, String body) throws Exception {
        var req = switch (method) {
            case "GET" -> get(url);
            case "POST" -> post(url);
            case "PUT" -> put(url);
            case "DELETE" -> delete(url);
            default -> throw new IllegalArgumentException(method);
        };
        req.contentType(MediaType.APPLICATION_JSON).content(body);
        if (token != null) req.header("Authorization", "Bearer " + token);
        return mockMvc.perform(req).andReturn().getResponse().getStatus();
    }

    // ---------------------------------------------------------------- catalog consistency

    @Test
    @DisplayName("Every authority checked by a controller exists in the permission catalog and ADMIN holds it")
    void catalogMatchesControllers() {
        Pattern p = Pattern.compile("hasAuthority\\('([^']+)'\\)");
        Set<String> used = new TreeSet<>();
        for (Object bean : context.getBeansWithAnnotation(RestController.class).values()) {
            Class<?> type = AopUtils.getTargetClass(bean);
            List<PreAuthorize> found = new ArrayList<>();
            PreAuthorize onClass = AnnotatedElementUtils.findMergedAnnotation(type, PreAuthorize.class);
            if (onClass != null) found.add(onClass);
            for (Method m : type.getDeclaredMethods()) {
                PreAuthorize a = AnnotatedElementUtils.findMergedAnnotation(m, PreAuthorize.class);
                if (a != null) found.add(a);
            }
            for (PreAuthorize a : found) {
                Matcher mt = p.matcher(a.value());
                while (mt.find()) used.add(mt.group(1));
            }
        }
        assertFalse(used.isEmpty());

        Set<String> catalog = new HashSet<>(jdbc.queryForList("SELECT CONCAT(method, ' ', api_path) FROM permissions", String.class));
        Set<String> admin = new HashSet<>(jdbc.queryForList(
                "SELECT CONCAT(p.method, ' ', p.api_path) FROM roles_permissions rp "
                        + "JOIN permissions p ON p.id = rp.permission_id JOIN roles r ON r.id = rp.role_id WHERE r.name = 'ADMIN'", String.class));
        for (String authority : used) {
            assertTrue(catalog.contains(authority), "controller checks '" + authority + "' but no such permission is seeded");
            assertTrue(admin.contains(authority), "ADMIN lacks '" + authority + "'");
        }
    }

    // ---------------------------------------------------------------- RECRUITER

    @Test
    @DisplayName("Recruiter: logged-in public reads and own-company endpoints work")
    void recruiterAllowed() throws Exception {
        assertEquals(200, call("GET", "/jobs", recruiterAToken));
        assertEquals(200, call("GET", "/companies", recruiterAToken));
        assertEquals(200, call("GET", "/jobs/company", recruiterAToken));
        assertEquals(200, call("GET", "/companies/me", recruiterAToken));
        assertEquals(200, call("GET", "/resumes/company", recruiterAToken));
    }

    @Test
    @DisplayName("Recruiter: admin-level (no ownership check) endpoints are forbidden")
    void recruiterForbiddenAdminEndpoints() throws Exception {
        assertEquals(403, call("PUT", "/jobs/" + jobOfB, recruiterAToken, jobJson()));
        assertEquals(403, call("DELETE", "/jobs/" + jobOfB, recruiterAToken));
        assertEquals(403, call("POST", "/jobs", recruiterAToken, jobJson()));
        assertEquals(403, call("DELETE", "/companies/" + companyAId, recruiterAToken));
        assertEquals(403, callCompany(org.springframework.http.HttpMethod.PUT, "/companies/" + companyAId, recruiterAToken));
        assertEquals(403, callCompany(org.springframework.http.HttpMethod.POST, "/companies", recruiterAToken));
        assertEquals(403, call("GET", "/resumes", recruiterAToken));
        assertEquals(403, call("PUT", "/resumes/status", recruiterAToken, "{\"id\":" + resumeOfCandidate + ",\"status\":\"APPROVED\"}"));
        assertEquals(403, call("GET", "/users", recruiterAToken));
        assertEquals(403, call("GET", "/roles", recruiterAToken));
        assertEquals(403, call("GET", "/permissions", recruiterAToken));
        assertEquals(403, call("POST", "/skills", recruiterAToken, "{\"name\":\"zzz\"}"));
    }

    @Test
    @DisplayName("Recruiter cannot touch another company's job through the recruiter endpoints either")
    void recruiterCannotCrossCompanies() throws Exception {
        assertEquals(403, call("PUT", "/jobs/company/" + jobOfB, recruiterAToken, jobJson()));
        assertEquals(403, call("DELETE", "/jobs/company/" + jobOfB, recruiterAToken));
        assertTrue(jobRepository.existsById(jobOfB), "job of company B must still exist");
    }

    // ---------------------------------------------------------------- USER (candidate)

    @Test
    @DisplayName("Candidate: public reads and own resume/subscription endpoints work")
    void candidateAllowed() throws Exception {
        assertEquals(200, call("GET", "/jobs", candidateToken));
        assertEquals(200, call("GET", "/skills", candidateToken));
        assertEquals(200, call("GET", "/resumes/me", candidateToken));
        assertEquals(200, call("GET", "/subscribers/me", candidateToken));
    }

    @Test
    @DisplayName("Candidate: recruiter/admin endpoints are forbidden")
    void candidateForbidden() throws Exception {
        assertEquals(403, call("POST", "/jobs/company", candidateToken, jobJson()));
        assertEquals(403, call("GET", "/jobs/company", candidateToken));
        assertEquals(403, call("GET", "/resumes/company", candidateToken));
        assertEquals(403, call("GET", "/resumes", candidateToken));
        assertEquals(403, call("GET", "/users", candidateToken));
        assertEquals(403, call("PUT", "/jobs/" + jobOfA, candidateToken, jobJson()));
    }

    // ---------------------------------------------------------------- anonymous / admin

    @Test
    @DisplayName("Anonymous is 401 (not 403) on protected endpoints; admin is allowed")
    void anonymousAndAdmin() throws Exception {
        assertEquals(401, call("GET", "/users", null));
        assertEquals(401, call("GET", "/resumes/me", null));
        assertEquals(200, call("GET", "/users", adminToken));
        assertEquals(200, call("GET", "/permissions", adminToken));
        assertEquals(200, call("GET", "/roles", adminToken));
    }

    @Test
    @DisplayName("403 body carries errorCode ACCESS_DENIED")
    void forbiddenBody() throws Exception {
        mockMvc.perform(get("/users").header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));
    }

    // ---------------------------------------------------------------- CV privacy

    @Test
    @DisplayName("A CV file link is only available to its owner, the receiving company's recruiters and admins")
    void resumeFileAccess() throws Exception {
        String url = "/resumes/file/" + resumeOfCandidate;
        assertEquals(200, call("GET", url, candidateToken), "owner");
        assertEquals(200, call("GET", url, recruiterBToken), "recruiter of the company that received it");
        assertEquals(200, call("GET", url, adminToken), "admin");
        assertEquals(403, call("GET", url, otherCandidateToken), "another candidate");
        assertEquals(403, call("GET", url, recruiterAToken), "recruiter of a different company");
        assertEquals(401, call("GET", url, null), "anonymous");
    }
}
