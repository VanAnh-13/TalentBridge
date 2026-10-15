package com.example.talentbridge.notification.infrastructure.realtime;

import com.example.talentbridge.notification.application.port.NotificationPublisher;
import com.example.talentbridge.notification.domain.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Pushes to {@code /user/queue/notifications} once the notification is committed, so a client that reacts by
 *  reloading its inbox always finds it. */
@Slf4j
@Component
@RequiredArgsConstructor
public class StompNotificationPublisher implements NotificationPublisher {

    private final SimpMessagingTemplate messaging;

    @Override
    public void push(Notification notification) {
        Runnable send = () -> {
            try {
                messaging.convertAndSendToUser(String.valueOf(notification.getUserId()), "/queue/notifications",
                        PushedNotification.of(notification));
            } catch (RuntimeException ex) {
                log.warn("Could not push notification {}: {}", notification.getId(), ex.getMessage());
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive())
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send.run();
                }
            });
        else
            send.run();
    }

    /** The JSON the browser receives; same shape as the REST response so the client can treat both alike. */
    record PushedNotification(Long id, String type, String title, String body, String link, boolean read,
                              java.time.Instant createdAt) {
        static PushedNotification of(Notification n) {
            return new PushedNotification(n.getId(), n.getType(), n.getTitle(), n.getBody(), n.getLink(), n.isRead(),
                    n.getCreatedAt());
        }
    }
}
