package com.example.talentbridge.notification.application;

import com.example.talentbridge.notification.application.port.NotificationPublisher;
import com.example.talentbridge.notification.domain.Notification;
import com.example.talentbridge.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The notification module's API for the others: tell a user something. */
@Service
@Transactional
@RequiredArgsConstructor
public class NotifyUser {

    private final NotificationRepository notifications;
    private final NotificationPublisher publisher;

    public Notification execute(Long userId, String type, String title, String body, String link) {
        Notification saved = notifications.save(Notification.create(userId, type, title, body, link));
        publisher.push(saved);
        return saved;
    }
}
