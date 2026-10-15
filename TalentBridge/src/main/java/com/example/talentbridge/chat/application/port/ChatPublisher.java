package com.example.talentbridge.chat.application.port;

import com.example.talentbridge.chat.domain.Message;

/** Output port: pushes chat events to people who are connected. */
public interface ChatPublisher {

    void messageSent(Long recipientId, Message message, String senderName);

    /** {@code readerId} has read everything up to {@code upToMessageId} of the conversation. */
    void messagesRead(Long recipientId, Long conversationId, Long readerId, Long upToMessageId);
}
