package com.example.talentbridge.chat.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/** How far a person has read a conversation. */
@Entity
@Table(name = "conversation_reads")
@IdClass(ConversationReadJpaEntity.Key.class)
@Getter
@Setter
@NoArgsConstructor
public class ConversationReadJpaEntity {

    public static class Key implements Serializable {
        private Long conversationId;
        private Long userId;

        public Key() {
        }

        public Key(Long conversationId, Long userId) {
            this.conversationId = conversationId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Key k && Objects.equals(conversationId, k.conversationId) && Objects.equals(userId, k.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(conversationId, userId);
        }
    }

    @Id
    @Column(name = "conversation_id")
    private Long conversationId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "last_read_message_id", nullable = false)
    private Long lastReadMessageId;
}
