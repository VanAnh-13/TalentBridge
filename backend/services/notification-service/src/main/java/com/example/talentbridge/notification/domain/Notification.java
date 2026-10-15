package com.example.talentbridge.notification.domain;

import com.example.talentbridge.shared.domain.exception.InvalidRequestException;

import java.time.Instant;

/** A message for one user, shown in their inbox and pushed live when they are online. */
public class Notification {

    private final Long id;
    private final Long userId;
    private final String type;
    private final String title;
    private final String body;
    private final String link;
    private final boolean read;
    private final Instant createdAt;

    private Notification(Long id, Long userId, String type, String title, String body, String link,
                         boolean read, Instant createdAt) {
        this.id = id;
        this.userId = require(userId, "Người nhận không được để trống");
        this.type = requireText(type, "Loại thông báo không được để trống");
        this.title = requireText(title, "Tiêu đề không được để trống");
        this.body = body;
        this.link = link;
        this.read = read;
        this.createdAt = createdAt;
    }

    public static Notification create(Long userId, String type, String title, String body, String link, Instant now) {
        return new Notification(null, userId, truncate(type, 50), truncate(title, 255), truncate(body, 1000),
                truncate(link, 255), false, now);
    }

    public static Notification restore(Long id, Long userId, String type, String title, String body, String link,
                                       boolean read, Instant createdAt) {
        return new Notification(id, userId, type, title, body, link, read, createdAt);
    }

    private static String truncate(String value, int max) {
        return value == null || value.length() <= max ? value : value.substring(0, max - 1) + "…";
    }

    private static <T> T require(T value, String message) {
        if (value == null) throw new InvalidRequestException(message);
        return value;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) throw new InvalidRequestException(message);
        return value;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getLink() { return link; }
    public boolean isRead() { return read; }
    public Instant getCreatedAt() { return createdAt; }
}
