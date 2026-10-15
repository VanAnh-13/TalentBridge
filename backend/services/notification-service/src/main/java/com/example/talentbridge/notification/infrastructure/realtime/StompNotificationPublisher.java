package com.example.talentbridge.notification.infrastructure.realtime;

import com.example.talentbridge.notification.application.port.NotificationPublisher;
import com.example.talentbridge.notification.domain.Notification;
import com.example.talentbridge.realtime.application.PushToUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

/** Pushes to {@code /user/queue/notifications} once the notification is committed, so a client that reacts by
 *  reloading its inbox always finds it. */
@Component
@RequiredArgsConstructor
public class StompNotificationPublisher implements NotificationPublisher {

    private final PushToUser pushToUser;

    /** The JSON the browser receives; same shape as the REST response so the client can treat both alike. */
    record PushedNotification(Long id, String type, String title, String body, String link, boolean read,
                              Instant createdAt) {
    }

    @Override
    public void push(Notification n) {
        pushToUser.execute(n.getUserId(), "notifications",
                new PushedNotification(n.getId(), n.getType(), n.getTitle(), n.getBody(), n.getLink(), n.isRead(), n.getCreatedAt()));
    }
}
