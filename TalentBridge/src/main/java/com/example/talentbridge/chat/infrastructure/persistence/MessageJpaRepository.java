package com.example.talentbridge.chat.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MessageJpaRepository extends JpaRepository<MessageJpaEntity, Long> {

    Page<MessageJpaEntity> findByConversationId(Long conversationId, Pageable pageable);

    Optional<MessageJpaEntity> findFirstByConversationIdOrderByIdDesc(Long conversationId);

    @Query(value = """
            select count(*) from chat_messages m
            where m.conversation_id = :conversationId and m.sender_id <> :userId
              and m.id > coalesce((select r.last_read_message_id from conversation_reads r
                                   where r.conversation_id = :conversationId and r.user_id = :userId), 0)
            """, nativeQuery = true)
    long unreadIn(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    @Query(value = """
            select count(*) from chat_messages m join conversations c on c.id = m.conversation_id
            where (c.candidate_id = :userId or c.company_id = :companyId) and m.sender_id <> :userId
              and m.id > coalesce((select r.last_read_message_id from conversation_reads r
                                   where r.conversation_id = c.id and r.user_id = :userId), 0)
            """, nativeQuery = true)
    long unreadFor(@Param("userId") Long userId, @Param("companyId") Long companyId);
}
