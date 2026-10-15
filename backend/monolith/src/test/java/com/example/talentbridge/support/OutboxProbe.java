package com.example.talentbridge.support;

import com.example.talentbridge.events.EventCodec;
import com.example.talentbridge.events.EventEnvelope;
import com.example.talentbridge.events.NotificationRequested;
import com.example.talentbridge.events.RealtimePush;
import com.example.talentbridge.shared.infrastructure.messaging.OutboxEventJpaEntity;
import com.example.talentbridge.shared.infrastructure.messaging.OutboxEventJpaRepository;

import java.util.Comparator;
import java.util.List;

/**
 * What the monolith asked other services to do, read back from the outbox: notifications go to the notification
 * service and live pushes to the browsers through it. Tests use user ids, which are unique per test.
 */
public final class OutboxProbe {

    private final OutboxEventJpaRepository rows;

    public OutboxProbe(OutboxEventJpaRepository rows) {
        this.rows = rows;
    }

    private List<EventEnvelope> events(String type) {
        return rows.findAll().stream()
                .filter(r -> type.equals(r.getEventType()))
                .sorted(Comparator.comparing(OutboxEventJpaEntity::getId))
                .map(r -> EventCodec.read(r.getPayload()))
                .toList();
    }

    /** Notifications requested for the user, oldest first. */
    public List<NotificationRequested> notificationsFor(long userId) {
        return events(NotificationRequested.TYPE).stream()
                .map(e -> EventCodec.payload(e, NotificationRequested.TYPE, 1, NotificationRequested.class))
                .filter(n -> n.userId() == userId)
                .toList();
    }

    public List<NotificationRequested> notificationsFor(long userId, String type) {
        return notificationsFor(userId).stream().filter(n -> type.equals(n.type())).toList();
    }

    public NotificationRequested latestNotificationFor(long userId) {
        List<NotificationRequested> all = notificationsFor(userId);
        if (all.isEmpty()) throw new AssertionError("no notification was requested for user " + userId);
        return all.get(all.size() - 1);
    }

    /** Live pushes to the user on a channel, oldest first. */
    public List<RealtimePush> pushesFor(long userId, String channel) {
        return events(RealtimePush.TYPE).stream()
                .map(e -> EventCodec.payload(e, RealtimePush.TYPE, 1, RealtimePush.class))
                .filter(p -> p.userId() == userId && channel.equals(p.channel()))
                .toList();
    }
}
