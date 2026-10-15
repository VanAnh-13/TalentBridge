package com.example.talentbridge.shared.infrastructure.messaging;

import com.example.talentbridge.events.EventCodec;
import com.example.talentbridge.events.EventEnvelope;
import com.example.talentbridge.events.NotificationRequested;
import com.example.talentbridge.events.Topics;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** The outbox against a real (in-process, KRaft) Kafka broker. */
@SpringBootTest(properties = {
        "app.events.relay.enabled=true",
        "app.events.relay.interval-ms=3600000",          // the test drives the relay itself
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@EmbeddedKafka(kraft = true, partitions = 1, topics = {Topics.NOTIFICATIONS, Topics.REALTIME})
@ActiveProfiles("test")
class OutboxRelayKafkaTest {

    @Autowired Outbox outbox;
    @Autowired OutboxRelay relay;
    @Autowired OutboxEventJpaRepository rows;
    @Autowired TransactionTemplate tx;
    @Autowired EmbeddedKafkaBroker broker;

    private Consumer<String, String> consumer() {
        Map<String, Object> props = KafkaTestUtils.consumerProps("outbox-test-" + System.nanoTime(), "false", broker);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        Consumer<String, String> consumer = new KafkaConsumer<>(props, new StringDeserializer(), new StringDeserializer());
        consumer.subscribe(List.of(Topics.NOTIFICATIONS));
        return consumer;
    }

    @Test @DisplayName("a committed event reaches Kafka once, keyed and enveloped, and is marked sent")
    void committedEventIsShipped() {
        relay.relay();   // anything left by start-up
        // a user id no real account can have: the test database is shared with other test classes
        EventEnvelope written = tx.execute(s -> outbox.add(Topics.NOTIFICATIONS, "-42", NotificationRequested.TYPE,
                NotificationRequested.VERSION, new NotificationRequested(-42, "TEST", "Xin chào", "body", "/x", false)));

        assertEquals(1, relay.relay());
        assertEquals(0, relay.relay(), "nothing is sent twice by the same relay");

        try (Consumer<String, String> consumer = consumer()) {
            ConsumerRecord<String, String> record = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(20)).records(Topics.NOTIFICATIONS)
                    .iterator().next();
            assertEquals("-42", record.key());
            EventEnvelope received = EventCodec.read(record.value());
            assertEquals(written.eventId(), received.eventId());
            NotificationRequested payload = EventCodec.payload(received, NotificationRequested.TYPE, 1, NotificationRequested.class);
            assertEquals("Xin chào", payload.title());
        }
        OutboxEventJpaEntity row = rows.findAll().stream().filter(r -> r.getEventId().equals(written.eventId())).findFirst().orElseThrow();
        assertNotNull(row.getPublishedAt());
        assertEquals(0, rows.countByPublishedAtIsNull());
    }

    @Test @DisplayName("an event of a rolled-back transaction never exists")
    void rolledBackEventIsNotWritten() {
        long before = rows.count();
        tx.executeWithoutResult(s -> {
            outbox.add(Topics.NOTIFICATIONS, "-1", NotificationRequested.TYPE, 1,
                    new NotificationRequested(-1, "TEST", "t", "b", "/", false));
            s.setRollbackOnly();
        });
        assertEquals(before, rows.count());
    }
}
