package com.example.talentbridge.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Characterisation of the /skills API: must stay identical across the Clean Architecture migration. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SkillControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    String adminToken;
    final String name = "SkillApi-" + System.nanoTime();
    Long id;

    @BeforeAll
    void login() throws Exception {
        adminToken = TestHelper.getAdminToken(mockMvc, objectMapper);
    }

    private ResultActions send(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder b) throws Exception {
        return mockMvc.perform(b.header("Authorization", "Bearer " + adminToken));
    }

    private String json(Object... kv) throws Exception {
        var node = objectMapper.createObjectNode();
        for (int i = 0; i < kv.length; i += 2) node.putPOJO((String) kv[i], kv[i + 1]);
        return objectMapper.writeValueAsString(node);
    }

    @Test
    @Order(1)
    @DisplayName("create skill")
    void create() throws Exception {
        String res = send(post("/skills").contentType(MediaType.APPLICATION_JSON).content(json("name", name)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.createdAt").isString())
                .andExpect(jsonPath("$.data.updatedAt").isString())
                .andReturn().getResponse().getContentAsString();
        id = objectMapper.readTree(res).get("data").get("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("create duplicate -> 409")
    void createDuplicate() throws Exception {
        send(post("/skills").contentType(MediaType.APPLICATION_JSON).content(json("name", name)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("ENTITY_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("This skill already exists"));
    }

    @Test
    @Order(3)
    @DisplayName("create with blank name -> 400 validation")
    void createBlank() throws Exception {
        send(post("/skills").contentType(MediaType.APPLICATION_JSON).content(json("name", "")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message", containsString("Skill name is required")));
    }

    @Test
    @Order(4)
    @DisplayName("get by id")
    void getById() throws Exception {
        send(get("/skills/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.name").value(name));
    }

    @Test
    @Order(5)
    @DisplayName("get missing -> 404")
    void getMissing() throws Exception {
        send(get("/skills/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("ENTITY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Skill not found"));
    }

    @Test
    @Order(6)
    @DisplayName("list: paging envelope, filter and sort")
    void list() throws Exception {
        send(get("/skills").param("filter", "name~'*SkillApi*'").param("page", "1").param("size", "5").param("sort", "name,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[*].name", hasItem(name)))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(5))
                .andExpect(jsonPath("$.data.totalElements").isNumber())
                .andExpect(jsonPath("$.data.totalPages").isNumber());

        send(get("/skills").param("filter", "name:'no-such-skill-xyz'"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    @Order(7)
    @DisplayName("update renames; duplicate name -> 409; missing -> 404")
    void update() throws Exception {
        String other = name + "-other";
        send(post("/skills").contentType(MediaType.APPLICATION_JSON).content(json("name", other))).andExpect(status().isOk());

        send(put("/skills").contentType(MediaType.APPLICATION_JSON).content(json("id", id, "name", other)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("ENTITY_ALREADY_EXISTS"));

        send(put("/skills").contentType(MediaType.APPLICATION_JSON).content(json("id", 999999, "name", "zzz")))
                .andExpect(status().isNotFound());

        send(put("/skills").contentType(MediaType.APPLICATION_JSON).content(json("id", id, "name", name + "-renamed")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(name + "-renamed"));

        // renaming to its own current name is allowed
        send(put("/skills").contentType(MediaType.APPLICATION_JSON).content(json("id", id, "name", name + "-renamed")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(8)
    @DisplayName("delete returns the deleted skill; second delete -> 404")
    void deleteSkill() throws Exception {
        send(delete("/skills/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id));
        send(delete("/skills/" + id)).andExpect(status().isNotFound());
        send(get("/skills/" + id)).andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @DisplayName("anonymous: 401 on write")
    void anonymous() throws Exception {
        mockMvc.perform(post("/skills").contentType(MediaType.APPLICATION_JSON).content(json("name", "x")))
                .andExpect(status().isUnauthorized());
    }
}
