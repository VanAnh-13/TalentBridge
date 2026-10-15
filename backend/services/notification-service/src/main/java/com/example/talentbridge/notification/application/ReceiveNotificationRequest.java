package com.example.talentbridge.notification.application;

import com.example.talentbridge.notification.application.port.NotificationPublisher;
import com.example.talentbridge.notification.domain.Notification;
import com.example.talentbridge.notification.domain.NotificationRepository;
import com.example.talentbridge.realtime.application.port.Presence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Optional;

/** Another service asked for someone to be told something (event NotificationRequested). */
@Service
@Transactional
@RequiredArgsConstructor
public class ReceiveNotificationRequest {

    private final NotificationRepository notifications;
    private final NotificationPublisher publisher;
    private final Presence presence;
    private final Clock clock;

    /**
     * Stores and pushes the notification, at most once per event. With {@code onlyIfOffline} nothing happens while the
     * user is connected: they already saw the thing live.
     *
     * @return the stored notification, or empty when it was skipped or is a duplicate
     */
    public Optional<Notification> execute(String eventId, long userId, String type, String title, String body,
                                          String link, boolean onlyIfOffline) {
        if (onlyIfOffline && presence.isOnline(userId)) return Optional.empty();
        Optional<Notification> saved = notifications.saveOnce(eventId,
                Notification.create(userId, type, title, body, link, clock.instant()));
        saved.ifPresent(publisher::push);
        return saved;
    }
}
