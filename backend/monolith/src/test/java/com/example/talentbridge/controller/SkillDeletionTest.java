package com.example.talentbridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Deleting a skill must neither orphan jobs nor wipe out the people who follow it. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SkillDeletionTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private long idOf(ResultActions r) throws Exception {
        return objectMapper.readTree(r.andReturn().getResponse().getContentAsString()).get("data").get("id").asLong();
    }

    @Test
    @DisplayName("a skill required by a job cannot be deleted; a skill only followed by subscribers can, and they stay")
    void deleteSkill() throws Exception {
        String sfx = String.valueOf(System.nanoTime());
        String admin = TestHelper.getAdminToken(mockMvc, objectMapper);
        String bearer = "Bearer " + admin;

        long skillId = idOf(mockMvc.perform(post("/skills").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"DelSkill-" + sfx + "\"}")).andExpect(status().isOk()));
        long otherSkill = idOf(mockMvc.perform(post("/skills").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"DelOther-" + sfx + "\"}")).andExpect(status().isOk()));

        MockMultipartFile company = new MockMultipartFile("company", "c.json", MediaType.APPLICATION_JSON_VALUE,
                ("{\"name\":\"DelCo " + sfx + "\",\"address\":\"HN\",\"description\":\"d\"}").getBytes());
        long companyId = idOf(mockMvc.perform(multipart("/companies").file(company).header("Authorization", bearer))
                .andExpect(status().isCreated()));
        long jobId = TestHelper.createJobForCompany(mockMvc, objectMapper, admin, companyId, skillId);

        String email = "del-" + sfx + "@test.com";
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"Passw0rd!x\",\"name\":\"Del\"}")).andExpect(status().isOk());
        String user = "Bearer " + TestHelper.getUserToken(mockMvc, objectMapper, email, "Passw0rd!x");
        mockMvc.perform(post("/subscribers/me").header("Authorization", user).contentType(MediaType.APPLICATION_JSON)
                .content("{\"skills\":[{\"id\":" + skillId + "},{\"id\":" + otherSkill + "}]}")).andExpect(status().isOk());

        // in use by a job: refused, nothing changes
        mockMvc.perform(delete("/skills/" + skillId).header("Authorization", bearer))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("BUSINESS_RULE_VIOLATION"));
        mockMvc.perform(get("/subscribers/me").header("Authorization", user))
                .andExpect(jsonPath("$.data.skills.length()").value(2));

        // once no job needs it, it can go and the subscriber keeps following the other skill
        mockMvc.perform(delete("/jobs/" + jobId).header("Authorization", bearer)).andExpect(status().is2xxSuccessful());
        mockMvc.perform(delete("/skills/" + skillId).header("Authorization", bearer)).andExpect(status().isOk());
        mockMvc.perform(get("/subscribers/me").header("Authorization", user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.skills.length()").value(1))
                .andExpect(jsonPath("$.data.skills[0].id").value(otherSkill));
    }
}
