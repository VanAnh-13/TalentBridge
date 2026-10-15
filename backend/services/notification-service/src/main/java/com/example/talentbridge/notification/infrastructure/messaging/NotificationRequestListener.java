package com.example.talentbridge.notification.infrastructure.messaging;

import com.example.talentbridge.events.EventCodec;
import com.example.talentbridge.events.EventEnvelope;
import com.example.talentbridge.events.NotificationRequested;
import com.example.talentbridge.events.Topics;
import com.example.talentbridge.notification.application.ReceiveNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes {@link Topics#NOTIFICATIONS}. A message that cannot be understood is logged and skipped (it would never
 * become readable by retrying); a failure while storing is thrown so the container retries it.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequestListener {

    private final ReceiveNotificationRequest receive;

    @KafkaListener(id = "notification-requests", topics = Topics.NOTIFICATIONS,
            groupId = "${app.events.group:notification-service}")
    public void on(String message) {
        EventEnvelope envelope;
        NotificationRequested request;
        try {
            envelope = EventCodec.read(message);
            request = EventCodec.payload(envelope, NotificationRequested.TYPE, NotificationRequested.VERSION,
                    NotificationRequested.class);
        } catch (IllegalArgumentException ex) {
            log.warn("Skipping an unreadable notification event: {}", ex.getMessage());
            return;
        }
        try {
            receive.execute(envelope.eventId(), request.userId(), request.type(), request.title(), request.body(),
                    request.link(), request.onlyIfOffline());
        } catch (com.example.talentbridge.shared.domain.exception.InvalidRequestException ex) {
            log.warn("Skipping notification event {}: {}", envelope.eventId(), ex.getMessage());
        }
    }
}
