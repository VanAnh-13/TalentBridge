package com.example.talentbridge.interview.infrastructure.messaging;

import com.example.talentbridge.interview.application.port.Notifier;
import com.example.talentbridge.shared.infrastructure.messaging.Outbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Notifications go to the notification service through the outbox; a failure never breaks the interview operation. */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventInterviewNotifier implements Notifier {

    private final Outbox outbox;

    @Override
    public void notify(Long userId, String type, String title, String body, String link) {
        try {
            outbox.requestNotification(userId, type, title, body, link, false);
        } catch (RuntimeException ex) {
            log.warn("Could not send the {} notification to user {}: {}", type, userId, ex.getMessage());
        }
    }
}
