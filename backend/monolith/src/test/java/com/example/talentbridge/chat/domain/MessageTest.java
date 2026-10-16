package com.example.talentbridge.chat.domain;

import com.example.talentbridge.shared.domain.exception.InvalidRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    private static final Instant NOW = Instant.parse("2030-01-01T00:00:00Z");

    @Test
    @DisplayName("text is trimmed and kept")
    void write() {
        Message m = Message.write(1L, 2L, "  hello \n", NOW);
        assertEquals("hello", m.getText());
        assertEquals(2L, m.getSenderId());
        assertEquals(NOW, m.getSentAt());
        assertNull(m.getId());
    }

    @Test
    @DisplayName("blank and oversized messages are refused")
    void invalid() {
        assertThrows(InvalidRequestException.class, () -> Message.write(1L, 2L, null, NOW));
        assertThrows(InvalidRequestException.class, () -> Message.write(1L, 2L, "   ", NOW));
        assertThrows(InvalidRequestException.class, () -> Message.write(1L, 2L, "x".repeat(Message.MAX_LENGTH + 1), NOW));
        assertDoesNotThrow(() -> Message.write(1L, 2L, "x".repeat(Message.MAX_LENGTH), NOW));
    }

    @Test
    @DisplayName("who is in a conversation: the candidate, and anyone working for its company")
    void participants() {
        Conversation c = Conversation.open(10L, 20L, 30L, NOW);
        assertTrue(c.hasParticipant(30L, null));
        assertTrue(c.hasParticipant(99L, 20L));
        assertFalse(c.hasParticipant(99L, 21L));
        assertFalse(c.hasParticipant(99L, null));
        assertEquals(NOW, c.getLastMessageAt());
    }
}
