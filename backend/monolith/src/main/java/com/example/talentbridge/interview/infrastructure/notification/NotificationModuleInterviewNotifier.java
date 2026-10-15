package com.example.talentbridge.interview.infrastructure.notification;

import com.example.talentbridge.interview.application.port.Notifier;
import com.example.talentbridge.notification.application.NotifyUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** A failure to notify is logged and never breaks the interview operation that caused it. */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationModuleInterviewNotifier implements Notifier {

    private final NotifyUser notifyUser;

    @Override
    public void notify(Long userId, String type, String title, String body, String link) {
        try {
            notifyUser.execute(userId, type, title, body, link);
        } catch (RuntimeException ex) {
            log.warn("Could not send the {} notification to user {}: {}", type, userId, ex.getMessage());
        }
    }
}
