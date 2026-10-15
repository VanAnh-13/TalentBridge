package com.example.talentbridge.chat.presentation;

import com.example.talentbridge.chat.application.ReadChats.ConversationView;
import com.example.talentbridge.chat.application.ReadChats.MessageView;
import com.example.talentbridge.chat.domain.Conversation;
import com.example.talentbridge.chat.domain.Message;

import java.time.Instant;

public final class ChatResponses {

    private ChatResponses() {
    }

    public record MessageResponse(Long id, Long conversationId, Long senderId, String senderName, String text, Instant sentAt) {
        public static MessageResponse from(MessageView v) {
            Message m = v.message();
            return new MessageResponse(m.getId(), m.getConversationId(), m.getSenderId(), v.senderName(), m.getText(), m.getSentAt());
        }

        public static MessageResponse from(Message m, String senderName) {
            return new MessageResponse(m.getId(), m.getConversationId(), m.getSenderId(), senderName, m.getText(), m.getSentAt());
        }
    }

    public record ConversationResponse(Long id, Long resumeId, String title, String subtitle, MessageResponse lastMessage,
                                       long unread, Instant lastMessageAt) {
        public static ConversationResponse from(ConversationView v) {
            Conversation c = v.conversation();
            return new ConversationResponse(c.getId(), c.getResumeId(), v.title(), v.subtitle(),
                    v.lastMessage() == null ? null : MessageResponse.from(v.lastMessage(), null), v.unread(), c.getLastMessageAt());
        }

        /** Just opened: nothing to show yet. */
        public static ConversationResponse of(Conversation c) {
            return new ConversationResponse(c.getId(), c.getResumeId(), null, null, null, 0, c.getLastMessageAt());
        }
    }
}
