package com.example.talentbridge.notification.application;

import com.example.talentbridge.notification.application.port.Accounts;
import com.example.talentbridge.notification.domain.Notification;
import com.example.talentbridge.notification.domain.NotificationRepository;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The signed-in user's inbox. */
@Service
@Transactional
@RequiredArgsConstructor
public class MyNotifications {

    private final NotificationRepository notifications;
    private final Accounts accounts;
    private final CurrentUser currentUser;

    @Transactional(readOnly = true)
    public PageResult<Notification> list(boolean unreadOnly, PageQuery page) {
        return notifications.findByUser(me(), unreadOnly, page);
    }

    @Transactional(readOnly = true)
    public long unreadCount() {
        return notifications.countUnread(me());
    }

    public void markRead(Long id) {
        if (!notifications.markRead(id, me()))
            throw new NotFoundException("Không tìm thấy thông báo");
    }

    public int markAllRead() {
        return notifications.markAllRead(me());
    }

    private Long me() {
        return accounts.idOfEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    }
}
