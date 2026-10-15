package com.example.talentbridge.interview.infrastructure.scheduling;

import com.example.talentbridge.interview.application.RemindInterviews;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Looks for reminders to send every five minutes. Runs in every instance: reminders are flagged as sent in the
 * database, so a second instance finds nothing left to do (a lock is only needed to avoid a rare duplicate).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InterviewReminderScheduler {

    private final RemindInterviews remindInterviews;

    @Scheduled(fixedDelayString = "${app.interview.reminder-interval-ms:300000}", initialDelay = 60000)
    public void remind() {
        try {
            remindInterviews.run();
        } catch (RuntimeException ex) {
            log.error("Interview reminders failed", ex);
        }
    }
}
