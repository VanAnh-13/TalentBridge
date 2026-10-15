package com.example.talentbridge.controller;

import com.example.talentbridge.model.User;
import com.example.talentbridge.model.constant.Gender;
import com.example.talentbridge.access.infrastructure.persistence.RoleJpaRepository;
import com.example.talentbridge.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Characterisation of /roles and /permissions: must stay identical across the Clean Architecture migration. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccessControlApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired RoleJpaRepository roleRepository;

    String adminToken;
    final String suffix = String.valueOf(System.nanoTime());
    long permId1, permId2, roleId;

    @BeforeAll
    void login() throws Exception {
        adminToken = TestHelper.getAdminToken(mockMvc, objectMapper);
    }

    private ResultActions admin(MockHttpServletRequestBuilder b, String body) throws Exception {
        b.header("Authorization", "Bearer " + adminToken);
        if (body != null) b.contentType(MediaType.APPLICATION_JSON).content(body);
        return mockMvc.perform(b);
    }

    private long idOf(ResultActions r) throws Exception {
        return objectMapper.readTree(r.andReturn().getResponse().getContentAsString()).get("data").get("id").asLong();
    }

    private String perm(String name, String path, String method) {
        return "{\"name\":\"" + name + "\",\"apiPath\":\"" + path + "\",\"method\":\"" + method + "\",\"module\":\"TESTMOD\"}";
    }

    private String role(String name, String desc, boolean active, long... permIds) {
        StringBuilder sb = new StringBuilder("{\"name\":\"" + name + "\",\"description\":\"" + desc + "\",\"active\":" + active + ",\"permissions\":[");
        for (int i = 0; i < permIds.length; i++) sb.append(i > 0 ? "," : "").append("{\"id\":").append(permIds[i]).append("}");
        return sb.append("]}").toString();
    }

    // ------------------------------------------------------------------ permissions

    @Test @Order(1) @DisplayName("permission: create (200), validation, duplicate (api_path+method) -> 409")
    void createPermission() throws Exception {
        ResultActions r = admin(post("/permissions"), perm("Perm One " + suffix, "/test-" + suffix + "/one", "GET"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Perm One " + suffix))
                .andExpect(jsonPath("$.data.apiPath").value("/test-" + suffix + "/one"))
                .andExpect(jsonPath("$.data.method").value("GET"))
                .andExpect(jsonPath("$.data.module").value("TESTMOD"))
                .andExpect(jsonPath("$.data.createdAt").isString());
        permId1 = idOf(r);
        permId2 = idOf(admin(post("/permissions"), perm("Perm Two " + suffix, "/test-" + suffix + "/two", "POST")));

        admin(post("/permissions"), perm("Dup", "/test-" + suffix + "/one", "GET")).andExpect(status().isConflict());
        admin(post("/permissions"), "{\"name\":\"\",\"apiPath\":\"\",\"method\":\"\",\"module\":\"\"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test @Order(2) @DisplayName("permission: list paged+filtered, list all unpaged")
    void listPermissions() throws Exception {
        admin(get("/permissions").param("filter", "module:'TESTMOD'").param("page", "1").param("size", "10"), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[*].name", hasItems("Perm One " + suffix, "Perm Two " + suffix)))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.totalElements").isNumber());

        admin(get("/permissions/all").param("filter", "module:'TESTMOD'"), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[*].name", hasItems("Perm One " + suffix, "Perm Two " + suffix)));
    }

    @Test @Order(3) @DisplayName("permission: update; missing -> 404")
    void updatePermission() throws Exception {
        admin(put("/permissions/" + permId2), perm("Perm Two Renamed " + suffix, "/test-" + suffix + "/two", "POST"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.name").value("Perm Two Renamed " + suffix));
        admin(put("/permissions/999999"), perm("x", "/x", "GET"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Không tìm thấy quyền hạn này"));
    }

    // ------------------------------------------------------------------ roles

    @Test @Order(4) @DisplayName("role: create (201) with permissions; always active; unknown permission -> 404; duplicate -> 409")
    void createRole() throws Exception {
        ResultActions r = admin(post("/roles"), role("CUSTOM_" + suffix, "custom role", false, permId1, permId2))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("CUSTOM_" + suffix))
                .andExpect(jsonPath("$.data.description").value("custom role"))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.data.permissions[*].id", containsInAnyOrder((int) permId1, (int) permId2)))
                .andExpect(jsonPath("$.data.permissions[0].apiPath").isString());
        roleId = idOf(r);

        admin(post("/roles"), role("OTHER_" + suffix, "d", true, 999999))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Quyền hạn không tồn tại"));
        admin(post("/roles"), role("CUSTOM_" + suffix, "dup", true, permId1)).andExpect(status().isConflict());
        admin(post("/roles"), "{\"name\":\"\",\"description\":\"\"}").andExpect(status().isBadRequest());
    }

    @Test @Order(5) @DisplayName("role: list paged+filtered")
    void listRoles() throws Exception {
        admin(get("/roles").param("filter", "name~'*CUSTOM_" + suffix + "*'").param("page", "1").param("size", "5"), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("CUSTOM_" + suffix))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.page").value(1));
    }

    @Test @Order(6) @DisplayName("role: update renames, changes active and replaces the permissions; missing -> 404")
    void updateRole() throws Exception {
        admin(put("/roles/" + roleId), role("CUSTOM2_" + suffix, "changed", false, permId2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("CUSTOM2_" + suffix))
                .andExpect(jsonPath("$.data.description").value("changed"))
                .andExpect(jsonPath("$.data.active").value(false))
                .andExpect(jsonPath("$.data.permissions.length()").value(1))
                .andExpect(jsonPath("$.data.permissions[0].id").value(permId2));

        // unknown permission ids in the request are ignored
        admin(put("/roles/" + roleId), role("CUSTOM2_" + suffix, "changed", true, permId1, 999999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.permissions[*].id", contains((int) permId1)));

        admin(put("/roles/999999"), role("X", "d", true)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Chức vụ không tồn tại"));
    }

    @Test @Order(7) @DisplayName("role: ADMIN/USER cannot be renamed (silently) nor deleted (403)")
    void builtInRoles() throws Exception {
        long adminRoleId = roleRepository.findByName("ADMIN").orElseThrow().getId();
        // resend ADMIN's current permissions: a PUT replaces them, an empty list would lock every admin out
        var current = objectMapper.readTree(admin(get("/roles").param("filter", "name:'ADMIN'"), null)
                .andReturn().getResponse().getContentAsString()).get("data").get("content").get(0).get("permissions");
        long[] ids = new long[current.size()];
        for (int i = 0; i < ids.length; i++) ids[i] = current.get(i).get("id").asLong();
        admin(put("/roles/" + adminRoleId), role("HACKED", "Quyền quản trị hệ thống", true, ids))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.name").value("ADMIN"))
                .andExpect(jsonPath("$.data.permissions.length()").value(ids.length));
        admin(delete("/roles/" + adminRoleId), null)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"))
                .andExpect(jsonPath("$.message").value("Không thể xóa chức vụ mặc định"));
        long userRoleId = roleRepository.findByName("USER").orElseThrow().getId();
        admin(delete("/roles/" + userRoleId), null).andExpect(status().isForbidden());
    }

    @Test @Order(7) @DisplayName("role: RECRUITER is built-in too (the app refuses to start without it)")
    void recruiterRoleIsProtected() throws Exception {
        long recruiterRoleId = roleRepository.findByName("RECRUITER").orElseThrow().getId();
        admin(delete("/roles/" + recruiterRoleId), null)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Không thể xóa chức vụ mặc định"));
        org.junit.jupiter.api.Assertions.assertTrue(roleRepository.findByName("RECRUITER").isPresent());
    }

    @Test @Order(8) @DisplayName("role: deleting detaches its users")
    void deleteRoleDetachesUsers() throws Exception {
        User u = new User();
        u.setEmail("roleholder-" + suffix + "@test.com");
        u.setName("holder");
        u.setPassword("x");
        u.setDob(LocalDate.of(2000, 1, 1));
        u.setAddress("a");
        u.setGender(Gender.OTHER);
        u.setRole(roleRepository.findById(roleId).orElseThrow());
        Long userId = userRepository.save(u).getId();

        admin(delete("/roles/" + roleId), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(roleId))
                .andExpect(jsonPath("$.data.name").value("CUSTOM2_" + suffix));

        assertNull(userRepository.findById(userId).orElseThrow().getRole(), "user must lose the deleted role");
        admin(delete("/roles/" + roleId), null).andExpect(status().isNotFound());
        // the permissions themselves survive
        admin(get("/permissions/all").param("filter", "module:'TESTMOD'"), null)
                .andExpect(jsonPath("$.data[*].id", hasItems((int) permId1, (int) permId2)));
    }

    @Test @Order(9) @DisplayName("permission: delete removes it from the roles; missing -> 404")
    void deletePermission() throws Exception {
        long r2 = idOf(admin(post("/roles"), role("HOLDER_" + suffix, "d", true, permId1)));
        admin(delete("/permissions/" + permId1), null)
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(permId1));
        admin(get("/roles").param("filter", "name:'HOLDER_" + suffix + "'"), null)
                .andExpect(jsonPath("$.data.content[0].permissions", empty()));
        admin(delete("/permissions/" + permId1), null).andExpect(status().isNotFound());
        admin(delete("/roles/" + r2), null).andExpect(status().isOk());
        admin(delete("/permissions/" + permId2), null).andExpect(status().isOk());
    }

    @Test @Order(10) @DisplayName("anonymous -> 401, non-admin -> 403")
    void security() throws Exception {
        mockMvc.perform(get("/roles")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/permissions")).andExpect(status().isUnauthorized());
    }
}
