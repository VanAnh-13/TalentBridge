package com.example.talentbridge.controller;

import com.example.talentbridge.init.DataValidator;
import com.example.talentbridge.model.User;
import com.example.talentbridge.model.constant.Gender;
import com.example.talentbridge.access.infrastructure.persistence.RoleJpaRepository;
import com.example.talentbridge.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityHardeningTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired RoleJpaRepository roleRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired DataValidator dataValidator;

    @Test
    @DisplayName("Anonymous users cannot write companies or jobs")
    void anonymousWritesAreRejected() throws Exception {
        String body = "{}";
        mockMvc.perform(post("/companies").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/companies/1").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/companies/1")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/jobs").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/jobs/1").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/jobs/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Public browsing of companies and jobs still works anonymously")
    void anonymousReadsStillWork() throws Exception {
        mockMvc.perform(get("/companies")).andExpect(status().isOk());
        mockMvc.perform(get("/jobs")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("A legacy admin@talentbridge.com / admin123 account is locked at startup")
    void legacyDefaultAdminIsNeutralized() throws Exception {
        User legacy = new User();
        legacy.setEmail("admin@talentbridge.com");
        legacy.setName("System Admin");
        legacy.setPassword(passwordEncoder.encode("admin123"));
        legacy.setDob(LocalDate.of(1990, 1, 1));
        legacy.setAddress("System");
        legacy.setGender(Gender.OTHER);
        legacy.setRole(roleRepository.findByName("ADMIN").orElseThrow());
        userRepository.save(legacy);

        dataValidator.run();

        String hash = userRepository.findByEmail("admin@talentbridge.com").orElseThrow().getPassword();
        assertFalse(passwordEncoder.matches("admin123", hash));

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@talentbridge.com\",\"password\":\"admin123\","
                                + "\"sessionMetaRequest\":{\"deviceName\":\"t\",\"deviceType\":\"t\",\"userAgent\":\"t\"}}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("No default admin is seeded by data.sql")
    void noSeededDefaultAdmin() {
        assertTrue(userRepository.findByEmail("admin@talentbridge.com").isEmpty()
                || !passwordEncoder.matches("admin123",
                userRepository.findByEmail("admin@talentbridge.com").get().getPassword()));
    }
}
