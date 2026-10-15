package com.example.talentbridge.chat;

import com.example.talentbridge.controller.TestHelper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    final String sfx = String.valueOf(System.nanoTime());
    String recruiterA, recruiterB, outsider, candidate, otherCandidate;
    long resumeId, conversationId;

    private String register(String email, boolean recruiter) throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"Passw0rd!x\",\"name\":\"" + email.split("-")[0] + "\",\"recruiter\":" + recruiter + "}"))
                .andExpect(status().isOk());
        return email;
    }

    private String token(String email) throws Exception {
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

    @BeforeAll
    void setUp() throws Exception {
        String admin = TestHelper.getAdminToken(mockMvc, objectMapper);
        long skill = id(call(post("/skills"), admin, "{\"name\":\"ChatSkill-" + sfx + "\"}"));
        String aMail = register("chata-" + sfx + "@test.com", true), bMail = register("chatb-" + sfx + "@test.com", true);
        recruiterA = token(aMail);
        long company = id(mockMvc.perform(multipart(HttpMethod.POST, "/companies/me")
                .file(new MockMultipartFile("company", "c.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"name\":\"ChatCo " + sfx + "\",\"description\":\"d\",\"address\":\"a\"}").getBytes()))
                .header("Authorization", "Bearer " + recruiterA)).andExpect(status().isCreated()));
        recruiterA = token(aMail);
        call(post("/companies/me/users"), recruiterA, "{\"email\":\"" + bMail + "\"}").andExpect(status().is2xxSuccessful());
        recruiterB = token(bMail);   // a colleague of A
        long job = id(call(post("/jobs/company"), recruiterA, "{\"name\":\"Chat Dev " + sfx + "\",\"location\":\"HCM\",\"salary\":1,\"quantity\":1,"
                + "\"level\":\"FRESHER\",\"description\":\"d\",\"startDate\":\"2030-01-01T00:00:00Z\",\"endDate\":\"2030-06-01T00:00:00Z\","
                + "\"active\":true,\"company\":{\"id\":" + company + "},\"skills\":[{\"id\":" + skill + "}]}"));

        String candMail = register("chatcand-" + sfx + "@test.com", false);
        candidate = token(candMail);
        otherCandidate = token(register("chatother-" + sfx + "@test.com", false));
        outsider = token(register("chatout-" + sfx + "@test.com", true));
        long candId = id(call(get("/auth/me"), candidate, null));
        resumeId = id(mockMvc.perform(multipart(HttpMethod.POST, "/resumes")
                .file(new MockMultipartFile("resume", "r.json", MediaType.APPLICATION_JSON_VALUE,
                        ("{\"email\":\"" + candMail + "\",\"status\":\"PENDING\",\"user\":{\"id\":" + candId + "},\"job\":{\"id\":" + job + "}}").getBytes()))
                .file(new MockMultipartFile("pdfFile", "cv.pdf", "application/pdf", "%PDF-1.4 cv".getBytes()))
                .header("Authorization", "Bearer " + candidate)).andExpect(status().isCreated()));
    }

    @Test @Order(1) @DisplayName("the candidate and the company's people can open the thread; strangers cannot; it is created once")
    void open() throws Exception {
        call(post("/chats/resumes/" + resumeId), null, null).andExpect(status().isUnauthorized());
        call(post("/chats/resumes/" + resumeId), otherCandidate, null).andExpect(status().isForbidden());
        call(post("/chats/resumes/" + resumeId), outsider, null).andExpect(status().isForbidden());
        call(post("/chats/resumes/999999"), candidate, null).andExpect(status().isNotFound());

        conversationId = id(call(post("/chats/resumes/" + resumeId), candidate, null).andExpect(status().isOk()));
        org.junit.jupiter.api.Assertions.assertEquals(conversationId, id(call(post("/chats/resumes/" + resumeId), recruiterA, null)));
        org.junit.jupiter.api.Assertions.assertEquals(conversationId, id(call(post("/chats/resumes/" + resumeId), recruiterB, null)));
    }

    @Test @Order(2) @DisplayName("only participants write; empty and oversized messages are refused")
    void send() throws Exception {
        String url = "/chats/" + conversationId + "/messages";
        call(post(url), otherCandidate, "{\"text\":\"hi\"}").andExpect(status().isForbidden());
        call(post(url), outsider, "{\"text\":\"hi\"}").andExpect(status().isForbidden());
        call(post("/chats/999999/messages"), candidate, "{\"text\":\"hi\"}").andExpect(status().isNotFound());
        call(post(url), candidate, "{\"text\":\"   \"}").andExpect(status().isBadRequest());
        call(post(url), candidate, "{\"text\":\"" + "x".repeat(2001) + "\"}").andExpect(status().isBadRequest());

        call(post(url), candidate, "{\"text\":\"Xin chào, em muốn hỏi về vị trí này\"}").andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.text").value("Xin chào, em muốn hỏi về vị trí này"));
        call(post(url), candidate, "{\"text\":\"Em nộp CV hôm qua ạ\"}").andExpect(status().isCreated());
    }

    @Test @Order(3) @DisplayName("offline recipients get ONE notification for a run of messages, and unread counts add up")
    void unreadAndNotifications() throws Exception {
        // both recruiters work for the company: both are told, once
        for (String recruiter : new String[]{recruiterA, recruiterB}) {
            call(get("/chats/unread-count"), recruiter, null).andExpect(jsonPath("$.data.count").value(2));
            call(get("/notifications"), recruiter, null)
                    .andExpect(jsonPath("$.data.content[?(@.type=='CHAT_MESSAGE')]", org.hamcrest.Matchers.hasSize(1)));
        }
        call(get("/chats/unread-count"), candidate, null).andExpect(jsonPath("$.data.count").value(0));

        call(get("/chats"), recruiterA, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(conversationId))
                .andExpect(jsonPath("$.data.content[0].unread").value(2))
                .andExpect(jsonPath("$.data.content[0].title", org.hamcrest.Matchers.containsString("chatcand-")))
                .andExpect(jsonPath("$.data.content[0].subtitle").value("Chat Dev " + sfx));
        call(get("/chats"), candidate, null)
                .andExpect(jsonPath("$.data.content[0].title").value("ChatCo " + sfx))
                .andExpect(jsonPath("$.data.content[0].unread").value(0));
        call(get("/chats"), otherCandidate, null).andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test @Order(4) @DisplayName("history is newest first and paged; reading is per person")
    void historyAndRead() throws Exception {
        String url = "/chats/" + conversationId + "/messages";
        call(get(url), otherCandidate, null).andExpect(status().isForbidden());
        call(get(url).param("page", "1").param("size", "1"), recruiterA, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(2))
                .andExpect(jsonPath("$.data.content[0].text").value("Em nộp CV hôm qua ạ"))
                .andExpect(jsonPath("$.data.content[0].senderName", org.hamcrest.Matchers.startsWith("chatcand")));

        call(post("/chats/" + conversationId + "/read"), otherCandidate, null).andExpect(status().isForbidden());
        call(post("/chats/" + conversationId + "/read"), recruiterA, null).andExpect(status().isNoContent());
        call(get("/chats/unread-count"), recruiterA, null).andExpect(jsonPath("$.data.count").value(0));
        call(get("/chats/unread-count"), recruiterB, null).andExpect(jsonPath("$.data.count").value(2));   // B has not read yet

        // A answers: the candidate gets a notification; A has nothing unread, B sees A's reply as unread
        call(post(url), recruiterA, "{\"text\":\"Chào em, hồ sơ đang được xem xét\"}").andExpect(status().isCreated());
        call(get("/chats/unread-count"), candidate, null).andExpect(jsonPath("$.data.count").value(1));
        call(get("/chats/unread-count"), recruiterA, null).andExpect(jsonPath("$.data.count").value(0));
        call(get("/chats/unread-count"), recruiterB, null).andExpect(jsonPath("$.data.count").value(3));
        call(get("/notifications"), candidate, null)
                .andExpect(jsonPath("$.data.content[?(@.type=='CHAT_MESSAGE')]", org.hamcrest.Matchers.hasSize(1)));
    }
}
