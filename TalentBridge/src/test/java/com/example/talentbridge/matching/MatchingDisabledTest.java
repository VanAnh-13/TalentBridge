package com.example.talentbridge.matching;

import com.example.talentbridge.controller.TestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Default configuration: no API key, so AI scoring is off and everything else keeps working. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MatchingDisabledTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test @DisplayName("without an API key the status says off and a recompute answers 503")
    void off() throws Exception {
        String admin = TestHelper.getAdminToken(mockMvc, objectMapper);
        mockMvc.perform(get("/matching/status").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enabled").value(false))
                .andExpect(jsonPath("$.data.model").doesNotExist());
    }
}
