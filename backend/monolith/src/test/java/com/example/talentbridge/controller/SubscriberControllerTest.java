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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Characterisation of /subscribers/me: must stay identical across the Clean Architecture migration. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SubscriberControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    final String email = "subscriber-" + System.nanoTime() + "@test.com";
    final String password = "Passw0rd!x";
    String adminToken, token;
    long skillA, skillB;
    final String nameA = "SubA-" + System.nanoTime(), nameB = "SubB-" + System.nanoTime();

    @BeforeAll
    void setUp() throws Exception {
        adminToken = TestHelper.getAdminToken(mockMvc, objectMapper);
        skillA = createSkill(nameA);
        skillB = createSkill(nameB);
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"name\":\"Sub User\"}"))
                .andExpect(status().isOk());
        token = TestHelper.getUserToken(mockMvc, objectMapper, email, password);
    }

    private long createSkill(String name) throws Exception {
        String res = mockMvc.perform(post("/skills").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(res).get("data").get("id").asLong();
    }

    private ResultActions as(MockHttpServletRequestBuilder b, String body) throws Exception {
        b.header("Authorization", "Bearer " + token);
        if (body != null) b.contentType(MediaType.APPLICATION_JSON).content(body);
        return mockMvc.perform(b);
    }

    private String skills(long... ids) {
        StringBuilder sb = new StringBuilder("{\"skills\":[");
        for (int i = 0; i < ids.length; i++) sb.append(i > 0 ? "," : "").append("{\"id\":").append(ids[i]).append("}");
        return sb.append("]}").toString();
    }

    @Test @Order(1) @DisplayName("not subscribed yet: GET returns null data, PUT/DELETE -> 404")
    void notSubscribed() throws Exception {
        as(get("/subscribers/me"), null).andExpect(status().isOk()).andExpect(jsonPath("$.data").doesNotExist());
        as(put("/subscribers/me"), skills(skillA)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("ENTITY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Subscription not found for this user"));
        as(delete("/subscribers/me"), null).andExpect(status().isNotFound());
    }

    @Test @Order(2) @DisplayName("validation: at least one skill")
    void validation() throws Exception {
        as(post("/subscribers/me"), "{\"skills\":[]}").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("At least one skill is required")));
        as(post("/subscribers/me"), "{}").andExpect(status().isBadRequest());
    }

    @Test @Order(3) @DisplayName("subscribe")
    void subscribe() throws Exception {
        as(post("/subscribers/me"), skills(skillA, skillB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.skills[*].name", containsInAnyOrder(nameA, nameB)))
                .andExpect(jsonPath("$.data.skills[*].id", containsInAnyOrder((int) skillA, (int) skillB)));
    }

    @Test @Order(4) @DisplayName("subscribing twice -> 409")
    void subscribeTwice() throws Exception {
        as(post("/subscribers/me"), skills(skillA)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("ENTITY_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("This user is already subscribed"));
    }

    @Test @Order(5) @DisplayName("get own subscription")
    void getOwn() throws Exception {
        as(get("/subscribers/me"), null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.skills[*].name", containsInAnyOrder(nameA, nameB)));
    }

    @Test @Order(6) @DisplayName("update replaces the skills; unknown skill ids are silently ignored")
    void updateOwn() throws Exception {
        as(put("/subscribers/me"), skills(skillB)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.skills[*].name", containsInAnyOrder(nameB)));
        as(get("/subscribers/me"), null).andExpect(jsonPath("$.data.skills.length()").value(1));

        as(put("/subscribers/me"), skills(999999)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.skills", empty()));
    }

    @Test @Order(7) @DisplayName("unsubscribe")
    void unsubscribe() throws Exception {
        as(delete("/subscribers/me"), null).andExpect(status().isNoContent());
        as(get("/subscribers/me"), null).andExpect(status().isOk()).andExpect(jsonPath("$.data").doesNotExist());
        as(delete("/subscribers/me"), null).andExpect(status().isNotFound());
    }

    @Test @Order(8) @DisplayName("deleting a subscription keeps the skills")
    void skillsSurvive() throws Exception {
        mockMvc.perform(get("/skills/" + skillA).header("Authorization", "Bearer " + adminToken)).andExpect(status().isOk());
        mockMvc.perform(get("/skills/" + skillB).header("Authorization", "Bearer " + adminToken)).andExpect(status().isOk());
    }

    @Test @Order(9) @DisplayName("anonymous -> 401")
    void anonymous() throws Exception {
        mockMvc.perform(get("/subscribers/me")).andExpect(status().isUnauthorized());
    }
}
