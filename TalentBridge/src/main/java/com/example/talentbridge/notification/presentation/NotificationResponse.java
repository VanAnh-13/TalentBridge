package com.example.talentbridge.notification.presentation;

import com.example.talentbridge.notification.domain.Notification;

import java.time.Instant;

public record NotificationResponse(Long id, String type, String title, String body, String link, boolean read,
                                   Instant createdAt) {

    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(n.getId(), n.getType(), n.getTitle(), n.getBody(), n.getLink(), n.isRead(),
                n.getCreatedAt());
    }
}
