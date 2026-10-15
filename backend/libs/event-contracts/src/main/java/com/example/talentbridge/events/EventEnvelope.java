package com.example.talentbridge.events;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;

/**
 * What every Kafka message carries. {@code eventId} is unique per event and lets consumers drop duplicates (delivery
 * is at least once); {@code type} and {@code version} say how to read {@code payload}.
 */
public record EventEnvelope(String eventId, String type, int version, Instant occurredAt, JsonNode payload) {
}
