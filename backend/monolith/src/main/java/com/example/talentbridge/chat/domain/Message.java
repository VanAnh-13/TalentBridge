package com.example.talentbridge.chat.domain;

import com.example.talentbridge.shared.domain.exception.InvalidRequestException;

import java.time.Instant;

public class Message {

    public static final int MAX_LENGTH = 2000;

    private final Long id;
    private final Long conversationId;
    private final Long senderId;
    private final String text;
    private final Instant sentAt;

    private Message(Long id, Long conversationId, Long senderId, String text, Instant sentAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.text = text;
        this.sentAt = sentAt;
    }

    public static Message write(Long conversationId, Long senderId, String text, Instant now) {
        if (text == null || text.isBlank())
            throw new InvalidRequestException("Message text is required");
        String trimmed = text.strip();
        if (trimmed.length() > MAX_LENGTH)
            throw new InvalidRequestException("A message can have at most " + MAX_LENGTH + " characters");
        return new Message(null, conversationId, senderId, trimmed, now);
    }

    public static Message restore(Long id, Long conversationId, Long senderId, String text, Instant sentAt) {
        return new Message(id, conversationId, senderId, text, sentAt);
    }

    public Long getId() { return id; }
    public Long getConversationId() { return conversationId; }
    public Long getSenderId() { return senderId; }
    public String getText() { return text; }
    public Instant getSentAt() { return sentAt; }
}
