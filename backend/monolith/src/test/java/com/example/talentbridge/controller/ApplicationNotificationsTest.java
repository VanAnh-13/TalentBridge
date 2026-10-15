package com.example.talentbridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("application submitted -> recruiter notified; status changed -> candidate notified; unchanged status -> silent")
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

        call(get("/notifications"), recruiter, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].type").value("APPLICATION_SUBMITTED"))
                .andExpect(jsonPath("$.data.content[0].body", containsString(candEmail)))
                .andExpect(jsonPath("$.data.content[0].body", containsString("Notif Dev " + sfx)));
        call(get("/notifications/unread-count"), candidate, null).andExpect(jsonPath("$.data.count").value(0));

        call(put("/resumes/company/status"), recruiter, "{\"id\":" + resume + ",\"status\":\"APPROVED\"}").andExpect(status().isOk());
        call(get("/notifications"), candidate, null)
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].type").value("APPLICATION_STATUS_CHANGED"))
                .andExpect(jsonPath("$.data.content[0].body", containsString("APPROVED")));

        call(put("/resumes/company/status"), recruiter, "{\"id\":" + resume + ",\"status\":\"APPROVED\"}").andExpect(status().isOk());
        call(get("/notifications/unread-count"), candidate, null).andExpect(jsonPath("$.data.count").value(1));
    }
}
