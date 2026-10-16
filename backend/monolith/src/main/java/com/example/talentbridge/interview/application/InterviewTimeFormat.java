package com.example.talentbridge.interview.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Writes interview times into notifications, in the time zone the platform's users live in ({@code app.time-zone}). */
@Component
class InterviewTimeFormat {

    private final DateTimeFormatter format;

    InterviewTimeFormat(@Value("${app.time-zone}") String timeZone) {
        this.format = DateTimeFormatter.ofPattern("HH:mm 'on' dd/MM/yyyy").withZone(ZoneId.of(timeZone));
    }

    String format(Instant instant) {
        return format.format(instant);
    }
}
