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

/** Characterisation of the /users API; must survive the migration unchanged. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    final String sfx = String.valueOf(System.nanoTime());
    final String email = "managed-" + sfx + "@test.com";
    final String password = "Passw0rd!x";
    String adminToken;
    long companyId, roleId, userId;

    @BeforeAll
    void setUp() throws Exception {
        adminToken = TestHelper.getAdminToken(mockMvc, objectMapper);
        String rec = TestHelper.getUserToken(mockMvc, objectMapper, register("owner-" + sfx + "@test.com", true), password);
        companyId = id(mockMvc.perform(multipart(HttpMethod.POST, "/companies/me")
                .file(new MockMultipartFile("company", "c.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"name\":\"UserCo " + sfx + "\",\"description\":\"d\",\"address\":\"a\"}").getBytes()))
                .header("Authorization", "Bearer " + rec)).andExpect(status().isCreated()));
        roleId = objectMapper.readTree(call(get("/roles").param("filter", "name:'USER'"), adminToken, null)
                .andReturn().getResponse().getContentAsString()).get("data").get("content").get(0).get("id").asLong();
    }

    private String register(String e, boolean recruiter) throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + e + "\",\"password\":\"" + password + "\",\"name\":\"n\",\"recruiter\":" + recruiter + "}"))
                .andExpect(status().isOk());
        return e;
    }

    private ResultActions call(MockHttpServletRequestBuilder b, String token, String body) throws Exception {
        if (token != null) b.header("Authorization", "Bearer " + token);
        if (body != null) b.contentType(MediaType.APPLICATION_JSON).content(body);
        return mockMvc.perform(b);
    }

    private long id(ResultActions r) throws Exception {
        return objectMapper.readTree(r.andReturn().getResponse().getContentAsString()).get("data").get("id").asLong();
    }

    private String login(String e, String p) throws Exception {
        return mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + e + "\",\"password\":\"" + p + "\",\"sessionMetaRequest\":{\"deviceName\":\"d\",\"deviceType\":\"d\",\"userAgent\":\"d\"}}"))
                .andReturn().getResponse().getContentAsString();
    }

    @Test @Order(1) @DisplayName("admin creates a user (201) with company and role; the password is trimmed")
    void create() throws Exception {
        ResultActions r = call(post("/users"), adminToken,
                "{\"name\":\"Managed\",\"email\":\"" + email + "\",\"password\":\"  " + password + "  \",\"dob\":\"1999-05-06\","
                        + "\"address\":\"Hue\",\"gender\":\"FEMALE\",\"company\":{\"id\":" + companyId + "},\"role\":{\"id\":" + roleId + "}}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.name").value("Managed"))
                .andExpect(jsonPath("$.data.dob").value("1999-05-06"))
                .andExpect(jsonPath("$.data.gender").value("FEMALE"))
                .andExpect(jsonPath("$.data.company.id").value(companyId))
                .andExpect(jsonPath("$.data.company.name").value("UserCo " + sfx))
                .andExpect(jsonPath("$.data.role.id").value(roleId))
                .andExpect(jsonPath("$.data.role.name").value("USER"))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.password").doesNotExist());
        userId = id(r);
        // the stored password is hashed and usable for login (trimmed)
        org.junit.jupiter.api.Assertions.assertTrue(login(email, password).contains("accessToken"));
    }

    @Test @Order(2) @DisplayName("create: duplicate email -> 409, unknown company/role -> 404, invalid -> 400")
    void createErrors() throws Exception {
        call(post("/users"), adminToken, "{\"name\":\"x\",\"email\":\"" + email + "\",\"password\":\"p\"}")
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("Email đã tồn tại"));
        call(post("/users"), adminToken, "{\"name\":\"x\",\"email\":\"c-" + email + "\",\"password\":\"p\",\"company\":{\"id\":999999}}")
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Không tìm thấy công ty"));
        call(post("/users"), adminToken, "{\"name\":\"x\",\"email\":\"r-" + email + "\",\"password\":\"p\",\"role\":{\"id\":999999}}")
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Không tìm thấy chức vụ"));
        call(post("/users"), adminToken, "{\"name\":\"\",\"email\":\"not-an-email\",\"password\":\"\"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test @Order(3) @DisplayName("list (paging, filter) and get by id")
    void read() throws Exception {
        call(get("/users").param("filter", "email:'" + email + "'").param("page", "1").param("size", "5"), adminToken, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(userId))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(5))
                .andExpect(jsonPath("$.data.totalElements").value(1));
        call(get("/users/" + userId), adminToken, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email));
        call(get("/users/999999"), adminToken, null).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Không tìm thấy người dùng"));
    }

    @Test @Order(4) @DisplayName("admin update: fields replaced, company/role -1 clears, unknown -> 404")
    void update() throws Exception {
        call(put("/users"), adminToken, "{\"id\":" + userId + ",\"name\":\"Renamed\",\"gender\":\"MALE\",\"dob\":\"2000-01-02\",\"address\":\"Hanoi\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Renamed"))
                .andExpect(jsonPath("$.data.gender").value("MALE"))
                .andExpect(jsonPath("$.data.address").value("Hanoi"))
                .andExpect(jsonPath("$.data.company.id").value(companyId))     // untouched when not sent
                .andExpect(jsonPath("$.data.role.name").value("USER"));
        call(put("/users"), adminToken, "{\"id\":" + userId + ",\"name\":\"Renamed\",\"company\":{\"id\":-1},\"role\":{\"id\":-1}}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.company").doesNotExist())
                .andExpect(jsonPath("$.data.role").doesNotExist());
        call(put("/users"), adminToken, "{\"id\":" + userId + ",\"name\":\"R\",\"role\":{\"id\":" + roleId + "}}")
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.role.id").value(roleId));
        call(put("/users"), adminToken, "{\"id\":999999,\"name\":\"R\"}").andExpect(status().isNotFound());
        call(put("/users"), adminToken, "{\"id\":" + userId + ",\"company\":{\"id\":999999}}").andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Không tìm thấy công ty"));
        call(put("/users"), adminToken, "{\"name\":\"no id\"}").andExpect(status().isBadRequest());
    }

    @Test @Order(5) @DisplayName("self service: profile, password, avatar")
    void selfService() throws Exception {
        String token = objectMapper.readTree(login(email, password)).get("data").get("accessToken").asText();

        call(post("/users/me/update-profile"), token, "{\"name\":\"Me\",\"gender\":\"OTHER\",\"dob\":\"1990-02-03\",\"address\":\"Home\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Me"))
                .andExpect(jsonPath("$.data.address").value("Home"));
        call(post("/users/me/update-profile"), token, "{\"name\":\"\"}").andExpect(status().isBadRequest());

        call(post("/users/me/update-password"), token, "{\"oldPassword\":\"wrong\",\"newPassword\":\"Newpassw0rd!\"}")
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("Mật khẩu hiện tại không chính xác"));
        call(post("/users/me/update-password"), token, "{\"oldPassword\":\"" + password + "\",\"newPassword\":\"Newpassw0rd!\"}")
                .andExpect(status().isOk());
        org.junit.jupiter.api.Assertions.assertTrue(login(email, "Newpassw0rd!").contains("accessToken"));
        org.junit.jupiter.api.Assertions.assertFalse(login(email, password).contains("accessToken"));

        mockMvc.perform(multipart("/users/me/upload-avatar")
                        .file(new MockMultipartFile("avatar", "a.png", MediaType.IMAGE_PNG_VALUE, "png".getBytes()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        call(get("/auth/me"), token, null).andExpect(jsonPath("$.data.logoUrl", containsString("avatar/" + userId)));
    }

    @Test @Order(6) @DisplayName("delete: removes the user; second delete -> 404")
    void deleteUser() throws Exception {
        call(delete("/users/" + userId), adminToken, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userId));
        call(get("/users/" + userId), adminToken, null).andExpect(status().isNotFound());
        call(delete("/users/" + userId), adminToken, null).andExpect(status().isNotFound());
        org.junit.jupiter.api.Assertions.assertFalse(login(email, "Newpassw0rd!").contains("accessToken"));
    }

    @Test @Order(7) @DisplayName("deleting a user who owns a company or has resumes works")
    void deleteOwnerWithResume() throws Exception {
        String ownerEmail = register("own2-" + sfx + "@test.com", true);
        String ownerToken = TestHelper.getUserToken(mockMvc, objectMapper, ownerEmail, password);
        long ownerId = id(call(get("/auth/me"), ownerToken, null));
        long cid = id(mockMvc.perform(multipart(HttpMethod.POST, "/companies/me")
                .file(new MockMultipartFile("company", "c.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"name\":\"OwnerCo " + sfx + "\",\"description\":\"d\",\"address\":\"a\"}").getBytes()))
                .header("Authorization", "Bearer " + ownerToken)).andExpect(status().isCreated()));

        call(delete("/users/" + ownerId), adminToken, null).andExpect(status().isOk());
        call(get("/companies/" + cid), null, null).andExpect(status().isOk());       // the company survives, ownerless
    }

    @Test @Order(8) @DisplayName("security: anonymous 401, candidate 403")
    void security() throws Exception {
        call(get("/users"), null, null).andExpect(status().isUnauthorized());
        String cand = TestHelper.getUserToken(mockMvc, objectMapper, register("plain-" + sfx + "@test.com", false), password);
        call(get("/users"), cand, null).andExpect(status().isForbidden());
        call(post("/users/me/update-profile"), null, "{}").andExpect(status().isUnauthorized());
    }
}
