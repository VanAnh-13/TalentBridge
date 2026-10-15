package com.example.talentbridge.chat.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationReadJpaRepository extends JpaRepository<ConversationReadJpaEntity, ConversationReadJpaEntity.Key> {
}
