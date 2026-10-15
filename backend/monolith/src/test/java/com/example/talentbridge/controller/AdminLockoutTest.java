package com.example.talentbridge.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Nobody may be able to lock every administrator out by editing roles or permissions. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminLockoutTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    String adminToken;
    JsonNode adminRole;

    @BeforeAll
    void setUp() throws Exception {
        adminToken = TestHelper.getAdminToken(mockMvc, objectMapper);
        adminRole = body(call(get("/roles").param("filter", "name:'ADMIN'"), null)).get("data").get("content").get(0);
    }

    private ResultActions call(MockHttpServletRequestBuilder b, String json) throws Exception {
        b.header("Authorization", "Bearer " + adminToken);
        if (json != null) b.contentType(MediaType.APPLICATION_JSON).content(json);
        return mockMvc.perform(b);
    }

    private JsonNode body(ResultActions r) throws Exception {
        return objectMapper.readTree(r.andReturn().getResponse().getContentAsString());
    }

    private String roleJson(String permissionsJson) {
        return "{\"name\":\"ADMIN\",\"description\":\"" + adminRole.get("description").asText()
                + "\",\"active\":true,\"permissions\":" + permissionsJson + "}";
    }

    private long permissionId(String method, String path) throws Exception {
        JsonNode all = body(call(get("/permissions/all"), null)).get("data");
        return StreamSupport.stream(all.spliterator(), false)
                .filter(p -> p.get("method").asText().equals(method) && p.get("apiPath").asText().equals(path))
                .findFirst().orElseThrow().get("id").asLong();
    }

    @Test
    @DisplayName("ADMIN cannot be stripped of the permissions needed to administer the system")
    void adminKeepsEssentialPermissions() throws Exception {
        long id = adminRole.get("id").asLong();

        call(put("/roles/" + id), roleJson("[]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.containsString("PUT /roles/{id}")));

        int before = adminRole.get("permissions").size();
        assertTrue(before > 0);
        assertEquals(before, body(call(get("/roles").param("filter", "name:'ADMIN'"), null))
                .get("data").get("content").get(0).get("permissions").size(), "a rejected update changes nothing");

        // sending the full list back is fine
        call(put("/roles/" + id), roleJson(adminRole.get("permissions").toString())).andExpect(status().isOk());
    }

    @Test
    @DisplayName("A permission ADMIN depends on can be neither deleted nor re-pointed, but can be renamed")
    void essentialPermissionsAreProtected() throws Exception {
        long id = permissionId("PUT", "/roles/{id}");

        call(delete("/permissions/" + id), null).andExpect(status().isForbidden());

        call(put("/permissions/" + id),
                "{\"name\":\"PUT /roles/{id}\",\"apiPath\":\"/roles/{id}/x\",\"method\":\"PUT\",\"module\":\"ROLE\"}")
                .andExpect(status().isForbidden());

        call(put("/permissions/" + id),
                "{\"name\":\"Update role\",\"apiPath\":\"/roles/{id}\",\"method\":\"PUT\",\"module\":\"ROLE\"}")
                .andExpect(status().isOk());
        call(put("/permissions/" + id),
                "{\"name\":\"PUT /roles/{id}\",\"apiPath\":\"/roles/{id}\",\"method\":\"PUT\",\"module\":\"ROLE\"}")
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("The essentials really are in the catalog")
    void essentialsExist() throws Exception {
        for (String authority : com.example.talentbridge.access.domain.Role.ADMIN_ESSENTIALS) {
            String[] parts = authority.split(" ", 2);
            permissionId(parts[0], parts[1]);   // throws when missing
        }
    }
}
