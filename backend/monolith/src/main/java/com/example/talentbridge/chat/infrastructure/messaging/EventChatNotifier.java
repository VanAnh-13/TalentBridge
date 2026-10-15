package com.example.talentbridge.chat.infrastructure.messaging;

import com.example.talentbridge.chat.application.port.Notifier;
import com.example.talentbridge.shared.infrastructure.messaging.Outbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Chat notifications are "only if offline": the notification service knows who is connected, this module does not. */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventChatNotifier implements Notifier {

    private final Outbox outbox;

    @Override
    public void notifyIfOffline(Long userId, String title, String body, String link) {
        try {
            outbox.requestNotification(userId, "CHAT_MESSAGE", title, body, link, true);
        } catch (RuntimeException ex) {
            log.warn("Could not notify user {} about a chat message: {}", userId, ex.getMessage());
        }
    }
}
