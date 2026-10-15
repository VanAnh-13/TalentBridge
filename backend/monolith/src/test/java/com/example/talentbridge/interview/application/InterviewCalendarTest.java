package com.example.talentbridge.interview.application;

import com.example.talentbridge.interview.domain.Interview;
import com.example.talentbridge.interview.domain.InterviewMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterviewCalendarTest {

    @Test
    @DisplayName("RFC 5545 text escaping")
    void escape() {
        assertEquals("a\\;b\\,c\\\\d\\nline", InterviewCalendar.escape("a;b,c\\d\nline"));
    }

    @Test
    @DisplayName("the invite has CRLF lines, UTC times and an end after the duration")
    void render() {
        Instant slot = Instant.parse("2030-03-10T09:00:00Z");
        Instant created = slot.minusSeconds(86400);
        Interview i = Interview.restore(7L, 1L, 2L, 3L, 4L, InterviewMode.ONLINE, "https://meet.example/x?a=1,2", null, 90,
                List.of(slot), slot, com.example.talentbridge.interview.domain.InterviewStatus.CONFIRMED, false, false, created);

        String ics = new InterviewCalendar(null, null, Clock.fixed(created, ZoneOffset.UTC)).render(i, "Phỏng vấn, Java", "mô tả");

        assertTrue(ics.startsWith("BEGIN:VCALENDAR\r\n"));
        assertTrue(ics.endsWith("END:VCALENDAR\r\n"));
        assertTrue(ics.contains("UID:interview-7@talentbridge\r\n"));
        assertTrue(ics.contains("DTSTART:20300310T090000Z\r\n"));
        assertTrue(ics.contains("DTEND:20300310T103000Z\r\n"));
        assertTrue(ics.contains("SUMMARY:Phỏng vấn\\, Java\r\n"));
        assertTrue(ics.contains("LOCATION:https://meet.example/x?a=1\\,2\r\n"));
    }
}
