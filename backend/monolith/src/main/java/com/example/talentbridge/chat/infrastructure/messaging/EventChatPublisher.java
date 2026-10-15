package com.example.talentbridge.chat.infrastructure.messaging;

import com.example.talentbridge.chat.application.port.ChatPublisher;
import com.example.talentbridge.chat.domain.Message;
import com.example.talentbridge.shared.infrastructure.messaging.Outbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Live chat events go to the notification service (which holds the browsers' WebSocket connections) through the
 * outbox: channel {@code chat} for new messages, {@code chat-read} for read receipts. The JSON is what the browser
 * receives, unchanged from when this module pushed it itself.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventChatPublisher implements ChatPublisher {

    private final Outbox outbox;

    record PushedMessage(Long id, Long conversationId, Long senderId, String senderName, String text, Instant sentAt) {
    }

    record ReadReceipt(Long conversationId, Long readerId, Long upToMessageId) {
    }

    @Override
    public void messageSent(Long recipientId, Message m, String senderName) {
        push(recipientId, "chat", new PushedMessage(m.getId(), m.getConversationId(), m.getSenderId(), senderName, m.getText(), m.getSentAt()));
    }

    @Override
    public void messagesRead(Long recipientId, Long conversationId, Long readerId, Long upToMessageId) {
        push(recipientId, "chat-read", new ReadReceipt(conversationId, readerId, upToMessageId));
    }

    private void push(Long recipientId, String channel, Object data) {
        try {
            outbox.pushLive(recipientId, channel, data);
        } catch (RuntimeException ex) {
            log.warn("Could not push a chat event to user {}: {}", recipientId, ex.getMessage());
        }
    }
}
