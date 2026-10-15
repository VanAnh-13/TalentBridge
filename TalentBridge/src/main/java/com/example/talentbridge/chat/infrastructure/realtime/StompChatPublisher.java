package com.example.talentbridge.chat.infrastructure.realtime;

import com.example.talentbridge.chat.application.port.ChatPublisher;
import com.example.talentbridge.chat.domain.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;

/** Pushes to {@code /user/queue/chat} (new messages) and {@code /user/queue/chat-read} (read receipts) after commit. */
@Slf4j
@Component
@RequiredArgsConstructor
public class StompChatPublisher implements ChatPublisher {

    private final SimpMessagingTemplate messaging;

    record PushedMessage(Long id, Long conversationId, Long senderId, String senderName, String text, Instant sentAt) {
    }

    record ReadReceipt(Long conversationId, Long readerId, Long upToMessageId) {
    }

    @Override
    public void messageSent(Long recipientId, Message m, String senderName) {
        afterCommit(() -> messaging.convertAndSendToUser(String.valueOf(recipientId), "/queue/chat",
                new PushedMessage(m.getId(), m.getConversationId(), m.getSenderId(), senderName, m.getText(), m.getSentAt())));
    }

    @Override
    public void messagesRead(Long recipientId, Long conversationId, Long readerId, Long upToMessageId) {
        afterCommit(() -> messaging.convertAndSendToUser(String.valueOf(recipientId), "/queue/chat-read",
                new ReadReceipt(conversationId, readerId, upToMessageId)));
    }

    private void afterCommit(Runnable send) {
        Runnable safe = () -> {
            try {
                send.run();
            } catch (RuntimeException ex) {
                log.warn("Could not push a chat event: {}", ex.getMessage());
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive())
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    safe.run();
                }
            });
        else
            safe.run();
    }
}
