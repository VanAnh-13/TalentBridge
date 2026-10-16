package com.example.talentbridge.events;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class EventCodecTest {

    @Test @DisplayName("an event survives the round trip and keeps its id, type, version and time")
    void roundTrip() {
        NotificationRequested sent = new NotificationRequested(7, "CHAT_MESSAGE", "New message", "Hello", "/chat", true);
        EventEnvelope envelope = EventCodec.wrap(NotificationRequested.TYPE, NotificationRequested.VERSION, sent,
                Instant.parse("2026-10-15T10:00:00Z"));
        EventEnvelope back = EventCodec.read(EventCodec.write(envelope));
        assertEquals(envelope.eventId(), back.eventId());
        assertEquals(Instant.parse("2026-10-15T10:00:00Z"), back.occurredAt());
        assertEquals(sent, EventCodec.payload(back, NotificationRequested.TYPE, 1, NotificationRequested.class));
        assertTrue(EventCodec.write(envelope).contains("\"occurredAt\":\"2026-10-15T10:00:00Z\""));
    }

    @Test @DisplayName("fields added by a newer producer are ignored; a newer major version or another type is refused")
    void evolution() {
        String json = "{\"eventId\":\"e1\",\"type\":\"RealtimePush\",\"version\":1,\"occurredAt\":\"2026-10-15T10:00:00Z\","
                + "\"traceId\":\"x\",\"payload\":{\"userId\":3,\"channel\":\"chat\",\"data\":{\"text\":\"hi\"},\"priority\":\"high\"}}";
        RealtimePush push = EventCodec.payload(EventCodec.read(json), RealtimePush.TYPE, 1, RealtimePush.class);
        assertEquals("hi", push.data().get("text").asText());

        EventEnvelope v2 = new EventEnvelope("e2", RealtimePush.TYPE, 2, Instant.now(), JsonNodeFactory.instance.objectNode());
        assertThrows(IllegalArgumentException.class, () -> EventCodec.payload(v2, RealtimePush.TYPE, 1, RealtimePush.class));
        assertThrows(IllegalArgumentException.class,
                () -> EventCodec.payload(EventCodec.read(json), NotificationRequested.TYPE, 1, NotificationRequested.class));
    }

    @Test @DisplayName("garbage and incomplete envelopes are refused, not half-read")
    void refusals() {
        assertThrows(IllegalArgumentException.class, () -> EventCodec.read("not json"));
        assertThrows(IllegalArgumentException.class, () -> EventCodec.read("{\"type\":\"RealtimePush\"}"));
    }

    @Test @DisplayName("channel names are safe to put in a destination")
    void channels() {
        assertTrue(RealtimePush.CHANNEL.matcher("chat-read").matches());
        assertFalse(RealtimePush.CHANNEL.matcher("../topic").matches());
        assertFalse(RealtimePush.CHANNEL.matcher("Chat").matches());
    }
}
