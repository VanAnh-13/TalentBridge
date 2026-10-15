package com.example.talentbridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.talentbridge.support.OutboxProbe;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Applying notifies the company's recruiters; a status change notifies the candidate. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApplicationNotificationsTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired com.example.talentbridge.shared.infrastructure.messaging.OutboxEventJpaRepository outbox;

    private String user(String email, boolean recruiter) throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"Passw0rd!x\",\"name\":\"n\",\"recruiter\":" + recruiter + "}"))
                .andExpect(status().isOk());
        return TestHelper.getUserToken(mockMvc, objectMapper, email, "Passw0rd!x");
    }

    private ResultActions call(MockHttpServletRequestBuilder b, String token, String body) throws Exception {
        b.header("Authorization", "Bearer " + token);
        if (body != null) b.contentType(MediaType.APPLICATION_JSON).content(body);
        return mockMvc.perform(b);
    }

    private long id(ResultActions r) throws Exception {
        return objectMapper.readTree(r.andReturn().getResponse().getContentAsString()).get("data").get("id").asLong();
    }

    @Test
    @DisplayName("application submitted -> recruiter notified; status changed -> candidate notified; unchanged status -> silent (as events for the notification service)")
    void notifications() throws Exception {
        String sfx = String.valueOf(System.nanoTime());
        String admin = TestHelper.getAdminToken(mockMvc, objectMapper);
        long skill = id(call(post("/skills"), admin, "{\"name\":\"NotifSkill-" + sfx + "\"}"));
        String recruiter = user("notif-rec-" + sfx + "@test.com", true);
        long company = id(mockMvc.perform(multipart(HttpMethod.POST, "/companies/me")
                .file(new MockMultipartFile("company", "c.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"name\":\"NotifCo " + sfx + "\",\"description\":\"d\",\"address\":\"a\"}").getBytes()))
                .header("Authorization", "Bearer " + recruiter)).andExpect(status().isCreated()));
        long job = id(call(post("/jobs/company"), recruiter,
                "{\"name\":\"Notif Dev " + sfx + "\",\"location\":\"HCM\",\"salary\":1,\"quantity\":1,\"level\":\"FRESHER\",\"description\":\"d\","
                        + "\"startDate\":\"2030-01-01T00:00:00Z\",\"endDate\":\"2030-06-01T00:00:00Z\",\"active\":true,"
                        + "\"company\":{\"id\":" + company + "},\"skills\":[{\"id\":" + skill + "}]}"));

        String candEmail = "notif-cand-" + sfx + "@test.com";
        String candidate = user(candEmail, false);
        long candId = id(call(get("/auth/me"), candidate, null));

        long resume = id(mockMvc.perform(multipart(HttpMethod.POST, "/resumes")
                .file(new MockMultipartFile("resume", "r.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"email\":\"" + candEmail + "\",\"status\":\"PENDING\",\"user\":{\"id\":" + candId + "},\"job\":{\"id\":" + job + "}}").getBytes()))
                .file(new MockMultipartFile("pdfFile", "cv.pdf", "application/pdf", "%PDF-1.4 cv".getBytes()))
                .header("Authorization", "Bearer " + candidate)).andExpect(status().isCreated()));

        long recruiterId = id(call(get("/auth/me"), recruiter, null));
        OutboxProbe probe = new OutboxProbe(outbox);
        var submitted = probe.notificationsFor(recruiterId);
        assertEquals(1, submitted.size());
        assertEquals("APPLICATION_SUBMITTED", submitted.get(0).type());
        assertThat(submitted.get(0).body()).contains(candEmail).contains("Notif Dev " + sfx);
        assertEquals("/recruiter/resumes", submitted.get(0).link());
        assertTrue(probe.notificationsFor(candId).isEmpty());

        call(put("/resumes/company/status"), recruiter, "{\"id\":" + resume + ",\"status\":\"APPROVED\"}").andExpect(status().isOk());
        var changed = probe.notificationsFor(candId);
        assertEquals(1, changed.size());
        assertEquals("APPLICATION_STATUS_CHANGED", changed.get(0).type());
        assertThat(changed.get(0).body()).contains("APPROVED");

        call(put("/resumes/company/status"), recruiter, "{\"id\":" + resume + ",\"status\":\"APPROVED\"}").andExpect(status().isOk());
        assertEquals(1, probe.notificationsFor(candId).size());
    }
}
