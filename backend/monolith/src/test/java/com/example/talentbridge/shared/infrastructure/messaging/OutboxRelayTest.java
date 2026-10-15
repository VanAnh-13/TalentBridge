package com.example.talentbridge.shared.infrastructure.messaging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OutboxRelayTest {

    private static OutboxEventJpaEntity row(long id) {
        OutboxEventJpaEntity e = new OutboxEventJpaEntity();
        e.setId(id);
        e.setEventId("e" + id);
        e.setTopic("t");
        e.setEventKey("k");
        e.setEventType("T");
        e.setPayload("{}");
        return e;
    }

    @Test @DisplayName("a failed send stops the batch so later events cannot overtake it; it is retried next time")
    @SuppressWarnings("unchecked")
    void failureStopsTheBatch() {
        OutboxEventJpaRepository repo = mock(OutboxEventJpaRepository.class);
        KafkaTemplate<String, String> kafka = mock(KafkaTemplate.class);
        OutboxEventJpaEntity first = row(1), second = row(2), third = row(3);
        when(repo.findPending(any(Pageable.class))).thenReturn(List.of(first, second, third));
        when(kafka.send(eq("t"), eq("k"), eq("{}")))
                .thenReturn(CompletableFuture.completedFuture((SendResult<String, String>) Mockito.mock(SendResult.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("broker down")));

        Clock clock = Clock.fixed(Instant.parse("2026-10-15T10:00:00Z"), ZoneOffset.UTC);
        int sent = new OutboxRelay(repo, kafka, clock).relay();

        assertEquals(1, sent);
        assertEquals(Instant.parse("2026-10-15T10:00:00Z"), first.getPublishedAt());
        assertNull(second.getPublishedAt());
        assertEquals(1, second.getAttempts());
        assertTrue(second.getLastError().contains("broker down"));
        assertNull(third.getPublishedAt());
        assertEquals(0, third.getAttempts());
        verify(kafka, times(2)).send(any(), any(), any());
    }
}
