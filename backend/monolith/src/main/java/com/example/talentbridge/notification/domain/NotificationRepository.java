package com.example.talentbridge.notification.domain;

import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;

public interface NotificationRepository {

    Notification save(Notification notification);

    /** Newest first. */
    PageResult<Notification> findByUser(Long userId, boolean unreadOnly, PageQuery page);

    long countUnread(Long userId);

    /** @return false when the notification does not exist or belongs to someone else */
    boolean markRead(Long id, Long userId);

    int markAllRead(Long userId);
}
