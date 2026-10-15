package com.example.talentbridge.notification.presentation;

import com.example.talentbridge.controller.TestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * POST /emails/job-recommendation/{email} used to be callable by anyone. It now needs its own permission,
 * which only ADMIN holds. The mail server is replaced by a mock, so nothing is really sent.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.mail.host=localhost", "management.health.mail.enabled=false"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DigestControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean JavaMailSender mailSender;

    final String email = "digest-" + System.nanoTime() + "@test.com";
    final String password = "Passw0rd!x";
    final String skillName = "DigestSkill-" + System.nanoTime();
    final String jobName = "Digest Job " + System.nanoTime();
    String adminToken, userToken;

    @BeforeAll
    void setUp() throws Exception {
        adminToken = TestHelper.getAdminToken(mockMvc, objectMapper);

        long skillId = idOf(mockMvc.perform(post("/skills").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"" + skillName + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        MockMultipartFile company = new MockMultipartFile("company", "company.json", MediaType.APPLICATION_JSON_VALUE,
                ("{\"name\":\"Digest Co " + System.nanoTime() + "\",\"address\":\"Hanoi\",\"description\":\"d\"}").getBytes());
        long companyId = idOf(mockMvc.perform(multipart("/companies").file(company)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        TestHelper.createJobForCompany(mockMvc, objectMapper, adminToken, companyId, skillId);

        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"name\":\"Digest User\"}"))
                .andExpect(status().isOk());
        userToken = TestHelper.getUserToken(mockMvc, objectMapper, email, password);
        mockMvc.perform(post("/subscribers/me").header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"skills\":[{\"id\":" + skillId + "}]}"))
                .andExpect(status().isOk());
    }

    /** Mockito beans are reset after every test, so the stub has to be renewed each time. */
    @BeforeEach
    void stubMimeMessage() {
        when(mailSender.createMimeMessage()).thenAnswer(i -> new MimeMessage((Session) null));
    }

    private long idOf(String body) throws Exception {
        return objectMapper.readTree(body).get("data").get("id").asLong();
    }

    @Test
    @DisplayName("anonymous callers get 401 and nothing is sent")
    void anonymous() throws Exception {
        mockMvc.perform(post("/emails/job-recommendation/" + email)).andExpect(status().isUnauthorized());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("a user without the permission gets 403 and nothing is sent")
    void withoutPermission() throws Exception {
        mockMvc.perform(post("/emails/job-recommendation/" + email).header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("admin sends the digest, which lists the job matching the subscriber's skill")
    void adminSends() throws Exception {
        mockMvc.perform(post("/emails/job-recommendation/" + email).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent successfully!"));

        ArgumentCaptor<MimeMessage> sent = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(sent.capture());
        assertTrue(sent.getValue().getAllRecipients()[0].toString().contains(email));
        assertTrue(sent.getValue().getContent() != null);
    }

    @Test
    @DisplayName("an unknown subscriber is reported as a failure")
    void unknownSubscriber() throws Exception {
        mockMvc.perform(post("/emails/job-recommendation/nobody-" + System.nanoTime() + "@test.com")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is(500))
                .andExpect(content().string(org.hamcrest.Matchers.startsWith("Email sending failed")));
    }
}
