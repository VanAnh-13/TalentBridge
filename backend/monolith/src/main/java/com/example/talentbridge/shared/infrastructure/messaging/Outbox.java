package com.example.talentbridge.shared.infrastructure.messaging;

import com.example.talentbridge.events.EventCodec;
import com.example.talentbridge.events.EventEnvelope;
import com.example.talentbridge.events.NotificationRequested;
import com.example.talentbridge.events.RealtimePush;
import com.example.talentbridge.events.Topics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;

/**
 * Where adapters put the events other services must hear about. The row joins the caller's transaction, so an event
 * exists exactly when the change it describes was committed; {@link OutboxRelay} ships it to Kafka afterwards.
 * Deliberately not {@code @Transactional} itself: a refused event (too large) throws before touching the database and
 * must not mark the caller's transaction as rollback-only.
 */
@Component
@RequiredArgsConstructor
public class Outbox {

    /** Room for the envelope around a payload; the column holds 8000 characters. */
    static final int MAX_LENGTH = 8000;

    private final OutboxEventJpaRepository events;
    private final Clock clock;

    /** Asks the notification service to put a message in a user's inbox. */
    public EventEnvelope requestNotification(long userId, String type, String title, String body, String link,
                                             boolean onlyIfOffline) {
        return add(Topics.NOTIFICATIONS, String.valueOf(userId), NotificationRequested.TYPE, NotificationRequested.VERSION,
                new NotificationRequested(userId, type, title, body, link, onlyIfOffline));
    }

    /** Asks the notification service to deliver {@code data} live on the user's {@code /user/queue/<channel>}. */
    public EventEnvelope pushLive(long userId, String channel, Object data) {
        return add(Topics.REALTIME, String.valueOf(userId), RealtimePush.TYPE, RealtimePush.VERSION,
                new RealtimePush(userId, channel, EventCodec.toJson(data)));
    }

    /** @param key Kafka key: events with the same key keep their order (e.g. the user id). */
    public EventEnvelope add(String topic, String key, String type, int version, Object payload) {
        EventEnvelope envelope = EventCodec.wrap(type, version, payload, clock.instant());
        String json = EventCodec.write(envelope);
        if (json.length() > MAX_LENGTH)
            throw new IllegalArgumentException(type + " event is too large for the outbox (" + json.length() + " chars)");

        OutboxEventJpaEntity row = new OutboxEventJpaEntity();
        row.setEventId(envelope.eventId());
        row.setTopic(topic);
        row.setEventKey(key);
        row.setEventType(type);
        row.setPayload(json);
        row.setCreatedAt(envelope.occurredAt());
        events.save(row);
        return envelope;
    }
}
