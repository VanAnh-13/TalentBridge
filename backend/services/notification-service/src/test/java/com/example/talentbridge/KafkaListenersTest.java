package com.example.talentbridge;

import com.example.talentbridge.events.EventCodec;
import com.example.talentbridge.events.EventEnvelope;
import com.example.talentbridge.events.NotificationRequested;
import com.example.talentbridge.events.RealtimePush;
import com.example.talentbridge.events.Topics;
import com.example.talentbridge.realtime.StompClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** What other services send through Kafka ends up in the inbox and in the browser. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.kafka.listener.auto-startup=true",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@EmbeddedKafka(kraft = true, partitions = 1, topics = {Topics.NOTIFICATIONS, Topics.REALTIME})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestTokens.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaListenersTest {

    @LocalServerPort int port;
    @Autowired EmbeddedKafkaBroker broker;
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper json;

    Producer<String, String> producer;

    @BeforeAll
    void producer() {
        producer = new KafkaProducer<>(KafkaTestUtils.producerProps(broker), new StringSerializer(), new StringSerializer());
    }

    @AfterAll
    void close() {
        producer.close();
    }

    private void send(String topic, long key, String value) throws Exception {
        producer.send(new ProducerRecord<>(topic, String.valueOf(key), value)).get(10, TimeUnit.SECONDS);
    }

    private long unread(long userId) throws Exception {
        String body = mockMvc.perform(get("/notifications/unread-count")
                .header("Authorization", "Bearer " + TestTokens.token(userId, TestTokens.INBOX))).andReturn().getResponse().getContentAsString();
        return json.readTree(body).get("data").get("count").asLong();
    }

    @Test @DisplayName("a requested notification lands in the inbox once, even when the event is delivered twice")
    void notificationFromEvent() throws Exception {
        long user = 9001;
        EventEnvelope event = EventCodec.wrap(NotificationRequested.TYPE, 1,
                new NotificationRequested(user, "APPLICATION_SUBMITTED", "Có ứng viên mới", "a@b.com", "/recruiter/resumes", false),
                Instant.now());
        send(Topics.NOTIFICATIONS, user, "this is not an event");             // skipped, does not block the partition
        send(Topics.NOTIFICATIONS, user, EventCodec.write(event));
        send(Topics.NOTIFICATIONS, user, EventCodec.write(event));             // redelivery
        await().atMost(Duration.ofSeconds(20)).until(() -> unread(user) >= 1);
        Thread.sleep(1500);
        assertEquals(1, unread(user));
    }

    @Test @DisplayName("a realtime push reaches the connected user's channel")
    void pushFromEvent() throws Exception {
        long user = 9002;
        StompSession session = StompClient.connect(port, TestTokens.token(user, TestTokens.INBOX));
        BlockingQueue<Map<String, Object>> reads = StompClient.listen(session, "/user/queue/chat-read");
        EventEnvelope event = EventCodec.wrap(RealtimePush.TYPE, 1,
                new RealtimePush(user, "chat-read", json.valueToTree(Map.of("conversationId", 3, "upToMessageId", 12))), Instant.now());
        send(Topics.REALTIME, user, EventCodec.write(event));
        Map<String, Object> received = reads.poll(20, TimeUnit.SECONDS);
        assertNotNull(received, "nothing pushed");
        assertEquals(12, received.get("upToMessageId"));
        send(Topics.REALTIME, user, EventCodec.write(event));                  // a duplicate is not pushed again
        assertNull(reads.poll(2, TimeUnit.SECONDS));
        session.disconnect();
    }
}
