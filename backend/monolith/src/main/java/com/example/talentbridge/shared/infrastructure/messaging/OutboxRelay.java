package com.example.talentbridge.shared.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Ships committed outbox rows to Kafka in insertion order and marks them sent. A row that cannot be sent stops the
 * batch (later events must not overtake it) and is retried on the next run. A crash between sending and marking
 * means the event goes out again: consumers drop duplicates by event id. On with {@code app.events.relay.enabled}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.events.relay", name = "enabled", havingValue = "true")
public class OutboxRelay {

    private static final int BATCH = 100;
    private static final Duration KEEP_SENT = Duration.ofDays(7);

    private final OutboxEventJpaRepository events;
    private final KafkaTemplate<String, String> kafka;
    private final Clock clock;

    @Scheduled(fixedDelayString = "${app.events.relay.interval-ms:500}")
    @Transactional
    public int relay() {
        List<OutboxEventJpaEntity> pending = events.findPending(PageRequest.of(0, BATCH));
        int sent = 0;
        for (OutboxEventJpaEntity event : pending) {
            try {
                kafka.send(event.getTopic(), event.getEventKey(), event.getPayload()).get(10, TimeUnit.SECONDS);
                event.setPublishedAt(clock.instant());
                event.setLastError(null);
                sent++;
            } catch (Exception ex) {
                if (ex instanceof InterruptedException) Thread.currentThread().interrupt();
                event.setAttempts(event.getAttempts() + 1);
                String message = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
                event.setLastError(message.length() > 500 ? message.substring(0, 500) : message);
                log.warn("Outbox event {} ({}) not sent, attempt {}: {}", event.getEventId(), event.getEventType(),
                        event.getAttempts(), message);
                break;
            }
        }
        return sent;
    }

    /** Sent events are kept a week for troubleshooting, then removed. */
    @Scheduled(cron = "${app.events.relay.purge-cron:0 30 3 * * *}")
    @Transactional
    public int purge() {
        return events.deletePublishedBefore(clock.instant().minus(KEEP_SENT));
    }
}
