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

/** Characterisation of the recruiter-facing company API (/companies/me...): must survive the migration unchanged. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecruiterCompanyApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    final String sfx = String.valueOf(System.nanoTime());
    final String password = "Passw0rd!x";
    final String emailA = "rec-a-" + sfx + "@test.com", emailB = "rec-b-" + sfx + "@test.com";
    final String companyName = "RecCo " + sfx;
    String adminToken, tokenA, tokenB;
    long companyId;

    @BeforeAll
    void setUp() throws Exception {
        adminToken = TestHelper.getAdminToken(mockMvc, objectMapper);
        for (String e : new String[]{emailA, emailB})
            mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"" + e + "\",\"password\":\"" + password + "\",\"name\":\"" + e + "\",\"recruiter\":true}"))
                    .andExpect(status().isOk());
        tokenA = TestHelper.getUserToken(mockMvc, objectMapper, emailA, password);
        tokenB = TestHelper.getUserToken(mockMvc, objectMapper, emailB, password);
    }

    private ResultActions json(MockHttpServletRequestBuilder b, String token, String body) throws Exception {
        b.header("Authorization", "Bearer " + token);
        if (body != null) b.contentType(MediaType.APPLICATION_JSON).content(body);
        return mockMvc.perform(b);
    }

    private ResultActions company(HttpMethod method, String url, String token, String name, String desc, String addr, boolean logo) throws Exception {
        var req = multipart(method, url)
                .file(new MockMultipartFile("company", "c.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"name\":\"" + name + "\",\"description\":\"" + desc + "\",\"address\":\"" + addr + "\"}").getBytes()));
        if (logo) req.file(new MockMultipartFile("logoFile", "logo.png", MediaType.IMAGE_PNG_VALUE, "png-bytes".getBytes()));
        return mockMvc.perform(req.header("Authorization", "Bearer " + token));
    }

    @Test @Order(1) @DisplayName("no company yet: /me and /me/users -> 404")
    void noCompanyYet() throws Exception {
        json(get("/companies/me"), tokenA, null).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Không tìm thấy công ty người dùng"));
        json(get("/companies/me/users"), tokenA, null).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Người dùng không có công ty"));
    }

    @Test @Order(2) @DisplayName("create own company (201); second one -> 409; duplicate name -> 409")
    void createOwnCompany() throws Exception {
        String res = company(HttpMethod.POST, "/companies/me", tokenA, companyName, "desc", "addr", false)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value(companyName))
                .andExpect(jsonPath("$.data.logoUrl").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        companyId = objectMapper.readTree(res).get("data").get("id").asLong();

        company(HttpMethod.POST, "/companies/me", tokenA, companyName + " 2", "d", "a", false)
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("Người dùng đã có công ty"));
        company(HttpMethod.POST, "/companies/me", tokenB, companyName.toUpperCase(), "d", "a", false)
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("Tên công ty đã tồn tại"));
        company(HttpMethod.POST, "/companies/me", tokenB, "", "d", "a", false).andExpect(status().isBadRequest());
    }

    @Test @Order(3) @DisplayName("get own company and its member list (owner flagged)")
    void getOwn() throws Exception {
        json(get("/companies/me"), tokenA, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(companyId))
                .andExpect(jsonPath("$.data.createdAt").isString());
        json(get("/companies/me/users"), tokenA, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].email").value(emailA))
                .andExpect(jsonPath("$.data[0].owner").value(true));
    }

    @Test @Order(4) @DisplayName("update own company incl. logo")
    void updateOwn() throws Exception {
        company(HttpMethod.PUT, "/companies/me", tokenA, companyName + " v2", "new desc", "new addr", true)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(companyName + " v2"))
                .andExpect(jsonPath("$.data.description").value("new desc"))
                .andExpect(jsonPath("$.data.address").value("new addr"))
                .andExpect(jsonPath("$.data.logoUrl", containsString("company-logos/" + companyId)));
        // recruiter without company
        company(HttpMethod.PUT, "/companies/me", tokenB, "x", "d", "a", false).andExpect(status().isNotFound());
        // public detail shows the logo and the listing carries jobsCount
        mockMvc.perform(get("/companies/" + companyId)).andExpect(jsonPath("$.data.logoUrl", containsString("company-logos")));
        mockMvc.perform(get("/companies/with-jobs-count").param("filter", "id:" + companyId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.content[0].jobsCount").value(0));
    }

    @Test @Order(5) @DisplayName("add a member: unknown / already has company -> 404, success -> 200")
    void addMember() throws Exception {
        json(post("/companies/me/users"), tokenA, "{\"email\":\"nobody-" + sfx + "@test.com\"}").andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Không tìm thấy người dùng cần thêm"));
        json(post("/companies/me/users"), tokenA, "{\"email\":\"" + emailB + "\"}").andExpect(status().isOk());
        json(post("/companies/me/users"), tokenA, "{\"email\":\"" + emailB + "\"}").andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Người dùng cần thêm đã có công ty"));
        json(post("/companies/me/users"), tokenA, "{}").andExpect(status().isBadRequest());

        json(get("/companies/me/users"), tokenB, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[?(@.email=='" + emailB + "')].owner", contains(false)))
                .andExpect(jsonPath("$.data[?(@.email=='" + emailA + "')].owner", contains(true)));
    }

    @Test @Order(6) @DisplayName("only the owner removes members")
    void removeMember() throws Exception {
        json(put("/companies/me/users"), tokenB, "{\"email\":\"" + emailA + "\"}").andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));
        json(put("/companies/me/users"), tokenA, "{\"email\":\"" + emailB + "\"}").andExpect(status().isOk());
        json(put("/companies/me/users"), tokenA, "{\"email\":\"" + emailB + "\"}").andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Người dùng cần loại bỏ không có công ty"));
        json(put("/companies/me/users"), tokenA, "{\"email\":\"nobody-" + sfx + "@test.com\"}").andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Không tìm thấy người dùng cần loại bỏ"));
        json(get("/companies/me/users"), tokenB, null).andExpect(status().isNotFound());
    }

    @Test @Order(7) @DisplayName("admin deletes the company; the owner no longer has one")
    void adminDeletes() throws Exception {
        json(delete("/companies/" + companyId), adminToken, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(companyId));
        json(get("/companies/me"), tokenA, null).andExpect(status().isNotFound());
        mockMvc.perform(get("/companies/" + companyId)).andExpect(status().isNotFound());
        json(delete("/companies/" + companyId), adminToken, null).andExpect(status().isNotFound());
    }
}
