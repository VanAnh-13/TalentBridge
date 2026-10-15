package com.example.talentbridge.notification.infrastructure.persistence;

import com.example.talentbridge.notification.domain.Notification;
import com.example.talentbridge.notification.domain.NotificationRepository;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import com.example.talentbridge.shared.infrastructure.persistence.PagingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final NotificationJpaRepository jpa;

    @Override
    public Optional<Notification> saveOnce(String eventId, Notification n) {
        // events with the same key arrive in order on one consumer, so check-then-insert is enough;
        // the unique column stops anything that slips through
        if (eventId != null && jpa.existsByEventId(eventId)) return Optional.empty();
        NotificationJpaEntity e = new NotificationJpaEntity();
        e.setEventId(eventId);
        e.setUserId(n.getUserId());
        e.setType(n.getType());
        e.setTitle(n.getTitle());
        e.setBody(n.getBody());
        e.setLink(n.getLink());
        e.setRead(n.isRead());
        e.setCreatedAt(n.getCreatedAt());
        return Optional.of(toDomain(jpa.saveAndFlush(e)));
    }

    @Override
    public PageResult<Notification> findByUser(Long userId, boolean unreadOnly, PageQuery page) {
        // newest first is the only order an inbox has
        PageRequest pageable = PageRequest.of(page.page(), page.size());
        var result = unreadOnly
                ? jpa.findByUserIdAndReadFalseOrderByCreatedAtDescIdDesc(userId, pageable)
                : jpa.findByUserIdOrderByCreatedAtDescIdDesc(userId, pageable);
        return PagingMapper.toPageResult(result, NotificationRepositoryAdapter::toDomain);
    }

    @Override
    public long countUnread(Long userId) {
        return jpa.countByUserIdAndReadFalse(userId);
    }

    @Override
    public boolean markRead(Long id, Long userId) {
        jpa.markRead(id, userId);
        return jpa.existsByIdAndUserId(id, userId);   // marking an already-read notification is fine
    }

    @Override
    public int markAllRead(Long userId) {
        return jpa.markAllRead(userId);
    }

    private static Notification toDomain(NotificationJpaEntity e) {
        return Notification.restore(e.getId(), e.getUserId(), e.getType(), e.getTitle(), e.getBody(), e.getLink(),
                e.isRead(), e.getCreatedAt());
    }
}
