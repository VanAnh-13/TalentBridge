package com.example.talentbridge.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Instant;
import java.util.UUID;

/** Writes and reads the JSON that goes over the wire, the same way on both sides. */
public final class EventCodec {

    private static final ObjectMapper JSON = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);   // newer producers may add fields

    private EventCodec() {
    }

    /** A new envelope around {@code payload}. */
    public static EventEnvelope wrap(String type, int version, Object payload, Instant occurredAt) {
        return new EventEnvelope(UUID.randomUUID().toString(), type, version, occurredAt, JSON.valueToTree(payload));
    }

    /** Any value as a JSON tree, e.g. the data of a {@link RealtimePush}. */
    public static com.fasterxml.jackson.databind.JsonNode toJson(Object value) {
        return JSON.valueToTree(value);
    }

    public static String write(EventEnvelope envelope) {
        try {
            return JSON.writeValueAsString(envelope);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Event cannot be written", ex);
        }
    }

    public static EventEnvelope read(String json) {
        try {
            EventEnvelope envelope = JSON.readValue(json, EventEnvelope.class);
            if (envelope.eventId() == null || envelope.type() == null || envelope.payload() == null)
                throw new IllegalArgumentException("Event is missing eventId, type or payload");
            return envelope;
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Event is not valid JSON", ex);
        }
    }

    /** The payload as {@code type}; fails when the event is of another type or a version this code does not know. */
    public static <T> T payload(EventEnvelope envelope, String expectedType, int knownVersion, Class<T> type) {
        if (!expectedType.equals(envelope.type()))
            throw new IllegalArgumentException("Expected " + expectedType + " but got " + envelope.type());
        if (envelope.version() > knownVersion)
            throw new IllegalArgumentException(expectedType + " v" + envelope.version() + " is newer than v" + knownVersion);
        try {
            return JSON.treeToValue(envelope.payload(), type);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Payload of " + expectedType + " cannot be read", ex);
        }
    }
}
