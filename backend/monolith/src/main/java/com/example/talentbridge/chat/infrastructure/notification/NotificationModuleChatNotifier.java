package com.example.talentbridge.chat.infrastructure.notification;

import com.example.talentbridge.chat.application.port.Notifier;
import com.example.talentbridge.notification.application.NotifyUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationModuleChatNotifier implements Notifier {

    private final NotifyUser notifyUser;

    @Override
    public void notify(Long userId, String title, String body, String link) {
        try {
            notifyUser.execute(userId, "CHAT_MESSAGE", title, body, link);
        } catch (RuntimeException ex) {
            log.warn("Could not notify user {} about a chat message: {}", userId, ex.getMessage());
        }
    }
}
