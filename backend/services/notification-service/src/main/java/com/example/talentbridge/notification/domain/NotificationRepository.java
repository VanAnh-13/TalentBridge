package com.example.talentbridge.notification.domain;

import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;

import java.util.Optional;

public interface NotificationRepository {

    /**
     * Stores the notification asked for by event {@code eventId}, once: the same event delivered again (delivery is
     * at least once) is ignored.
     *
     * @return the stored notification, or empty when this event was already handled
     */
    Optional<Notification> saveOnce(String eventId, Notification notification);

    /** Newest first. */
    PageResult<Notification> findByUser(Long userId, boolean unreadOnly, PageQuery page);

    long countUnread(Long userId);

    /** @return false when the notification does not exist or belongs to someone else */
    boolean markRead(Long id, Long userId);

    int markAllRead(Long userId);
}
