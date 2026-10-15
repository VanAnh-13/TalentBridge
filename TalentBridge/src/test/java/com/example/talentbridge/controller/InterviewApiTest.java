package com.example.talentbridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
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

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InterviewApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    final String sfx = String.valueOf(System.nanoTime());
    String recruiter, colleague, outsider, candidate, otherCandidate;
    long resumeId, interviewId;
    final Instant day1 = Instant.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
    final Instant day2 = day1.plus(Duration.ofDays(1));

    private String user(String email, boolean recruiterRole) throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"Passw0rd!x\",\"name\":\"" + email.split("-")[0] + "\",\"recruiter\":" + recruiterRole + "}"))
                .andExpect(status().isOk());
        return TestHelper.getUserToken(mockMvc, objectMapper, email, "Passw0rd!x");
    }

    private ResultActions call(MockHttpServletRequestBuilder b, String token, String body) throws Exception {
        if (token != null) b.header("Authorization", "Bearer " + token);
        if (body != null) b.contentType(MediaType.APPLICATION_JSON).content(body);
        return mockMvc.perform(b);
    }

    private long id(ResultActions r) throws Exception {
        return objectMapper.readTree(r.andReturn().getResponse().getContentAsString()).get("data").get("id").asLong();
    }

    private String body(long resume, Instant... slots) {
        StringBuilder s = new StringBuilder("[");
        for (int i = 0; i < slots.length; i++) s.append(i > 0 ? "," : "").append('"').append(slots[i]).append('"');
        return "{\"resumeId\":" + resume + ",\"mode\":\"ONLINE\",\"location\":\"https://meet.example/" + sfx + "\",\"note\":\"Bring a laptop\","
                + "\"durationMinutes\":45,\"slots\":" + s + "]}";
    }

    @BeforeAll
    void setUp() throws Exception {
        String admin = TestHelper.getAdminToken(mockMvc, objectMapper);
        long skill = id(call(post("/skills"), admin, "{\"name\":\"IvSkill-" + sfx + "\"}"));
        recruiter = user("ivrec-" + sfx + "@test.com", true);
        long company = id(mockMvc.perform(multipart(HttpMethod.POST, "/companies/me")
                .file(new MockMultipartFile("company", "c.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"name\":\"IvCo " + sfx + "\",\"description\":\"d\",\"address\":\"a\"}").getBytes()))
                .header("Authorization", "Bearer " + recruiter)).andExpect(status().isCreated()));
        recruiter = TestHelper.getUserToken(mockMvc, objectMapper, "ivrec-" + sfx + "@test.com", "Passw0rd!x");
        long job = id(call(post("/jobs/company"), recruiter, "{\"name\":\"Iv Dev " + sfx + "\",\"location\":\"HCM\",\"salary\":1,\"quantity\":1,"
                + "\"level\":\"FRESHER\",\"description\":\"d\",\"startDate\":\"2030-01-01T00:00:00Z\",\"endDate\":\"2030-06-01T00:00:00Z\","
                + "\"active\":true,\"company\":{\"id\":" + company + "},\"skills\":[{\"id\":" + skill + "}]}"));

        String candEmail = "ivcand-" + sfx + "@test.com";
        candidate = user(candEmail, false);
        otherCandidate = user("ivother-" + sfx + "@test.com", false);
        outsider = user("ivout-" + sfx + "@test.com", true);   // a recruiter of no company
        long candId = id(call(get("/auth/me"), candidate, null));
        resumeId = id(mockMvc.perform(multipart(HttpMethod.POST, "/resumes")
                .file(new MockMultipartFile("resume", "r.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"email\":\"" + candEmail + "\",\"status\":\"PENDING\",\"user\":{\"id\":" + candId + "},\"job\":{\"id\":" + job + "}}").getBytes()))
                .file(new MockMultipartFile("pdfFile", "cv.pdf", "application/pdf", "%PDF-1.4 cv".getBytes()))
                .header("Authorization", "Bearer " + candidate)).andExpect(status().isCreated()));
    }

    @Test @Order(1) @DisplayName("only a recruiter of the company can propose; validation and duplicates are enforced")
    void schedule() throws Exception {
        call(post("/interviews"), null, body(resumeId, day1)).andExpect(status().isUnauthorized());
        call(post("/interviews"), candidate, body(resumeId, day1)).andExpect(status().isForbidden());
        call(post("/interviews"), outsider, body(resumeId, day1)).andExpect(status().isForbidden());
        call(post("/interviews"), recruiter, body(999999, day1)).andExpect(status().isNotFound());
        call(post("/interviews"), recruiter, body(resumeId)).andExpect(status().isBadRequest());
        call(post("/interviews"), recruiter, body(resumeId, Instant.now().minusSeconds(60))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));

        interviewId = id(call(post("/interviews"), recruiter, body(resumeId, day2, day1)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PROPOSED"))
                .andExpect(jsonPath("$.data.proposedSlots.length()").value(2))
                .andExpect(jsonPath("$.data.durationMinutes").value(45)));

        call(post("/interviews"), recruiter, body(resumeId, day1)).andExpect(status().isConflict());

        // the candidate was told
        call(get("/notifications"), candidate, null).andExpect(jsonPath("$.data.content[0].type").value("INTERVIEW_PROPOSED"))
                .andExpect(jsonPath("$.data.content[0].link").value("/user/interviews"));
    }

    @Test @Order(2) @DisplayName("lists: the candidate sees theirs with the job; the company sees its own; others see nothing")
    void lists() throws Exception {
        call(get("/interviews/me"), candidate, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(interviewId))
                .andExpect(jsonPath("$.data.content[0].jobName").value("Iv Dev " + sfx))
                .andExpect(jsonPath("$.data.content[0].companyName").value("IvCo " + sfx));
        call(get("/interviews/me"), otherCandidate, null).andExpect(jsonPath("$.data.totalElements").value(0));
        call(get("/interviews/company"), recruiter, null).andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].candidateEmail", containsString("ivcand-")));
        call(get("/interviews/company"), outsider, null).andExpect(status().isNotFound());   // no company yet
        call(get("/interviews/company"), candidate, null).andExpect(status().isForbidden());
    }

    @Test @Order(3) @DisplayName("only the candidate answers, and only with an offered slot")
    void confirm() throws Exception {
        call(put("/interviews/" + interviewId + "/confirm"), otherCandidate, "{\"slot\":\"" + day1 + "\"}").andExpect(status().isForbidden());
        call(put("/interviews/" + interviewId + "/confirm"), recruiter, "{\"slot\":\"" + day1 + "\"}").andExpect(status().isForbidden());
        call(put("/interviews/" + interviewId + "/confirm"), candidate, "{\"slot\":\"" + day1.plusSeconds(3600) + "\"}").andExpect(status().isBadRequest());
        call(put("/interviews/" + interviewId + "/confirm"), candidate, "{}").andExpect(status().isBadRequest());

        call(get("/interviews/" + interviewId + "/calendar.ics"), candidate, null).andExpect(status().isConflict());   // not confirmed yet

        call(put("/interviews/" + interviewId + "/confirm"), candidate, "{\"slot\":\"" + day1 + "\"}").andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.confirmedSlot").value(day1.toString()));
        call(put("/interviews/" + interviewId + "/confirm"), candidate, "{\"slot\":\"" + day1 + "\"}").andExpect(status().isConflict());

        call(get("/notifications"), recruiter, null).andExpect(jsonPath("$.data.content[0].type").value("INTERVIEW_CONFIRMED"));
    }

    @Test @Order(4) @DisplayName("the calendar file is for the two parties only")
    void calendar() throws Exception {
        for (String party : new String[]{candidate, recruiter}) {
            call(get("/interviews/" + interviewId + "/calendar.ics"), party, null).andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", containsString("text/calendar")))
                    .andExpect(header().string("Content-Disposition", containsString("interview-" + interviewId + ".ics")))
                    .andExpect(content().string(containsString("BEGIN:VEVENT")))
                    .andExpect(content().string(containsString("SUMMARY:Phỏng vấn Iv Dev " + sfx)));
        }
        call(get("/interviews/" + interviewId + "/calendar.ics"), otherCandidate, null).andExpect(status().isForbidden());
        call(get("/interviews/" + interviewId + "/calendar.ics"), outsider, null).andExpect(status().isForbidden());
        call(get("/interviews/999999/calendar.ics"), candidate, null).andExpect(status().isNotFound());
    }

    @Test @Order(5) @DisplayName("the recruiter reschedules (candidate must answer again), then cancels; nobody else may")
    void rescheduleAndCancel() throws Exception {
        call(put("/interviews/" + interviewId + "/reschedule"), outsider, body(resumeId, day2)).andExpect(status().isForbidden());
        call(put("/interviews/" + interviewId + "/reschedule"), candidate, body(resumeId, day2)).andExpect(status().isForbidden());

        call(put("/interviews/" + interviewId + "/reschedule"), recruiter,
                "{\"mode\":\"ONSITE\",\"location\":\"12 Nguyen Hue\",\"durationMinutes\":30,\"slots\":[\"" + day2 + "\"]}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PROPOSED"))
                .andExpect(jsonPath("$.data.mode").value("ONSITE"))
                .andExpect(jsonPath("$.data.confirmedSlot").doesNotExist());
        call(get("/notifications"), candidate, null).andExpect(jsonPath("$.data.content[0].type").value("INTERVIEW_RESCHEDULED"));

        call(put("/interviews/" + interviewId + "/decline"), candidate, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DECLINED"));
        call(get("/notifications"), recruiter, null).andExpect(jsonPath("$.data.content[0].type").value("INTERVIEW_DECLINED"));

        // a declined interview can be offered again
        call(put("/interviews/" + interviewId + "/reschedule"), recruiter, body(resumeId, day1, day2)).andExpect(status().isOk());

        call(put("/interviews/" + interviewId + "/cancel"), outsider, null).andExpect(status().isForbidden());
        call(put("/interviews/" + interviewId + "/cancel"), recruiter, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
        call(get("/notifications"), candidate, null).andExpect(jsonPath("$.data.content[0].type").value("INTERVIEW_CANCELLED"));
        call(put("/interviews/" + interviewId + "/cancel"), recruiter, null).andExpect(status().isConflict());
        call(put("/interviews/" + interviewId + "/reschedule"), recruiter, body(resumeId, day1)).andExpect(status().isConflict());

        // once cancelled, a new interview for the same application is allowed
        call(post("/interviews"), recruiter, body(resumeId, day1)).andExpect(status().isCreated());
    }
}
