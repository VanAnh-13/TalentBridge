package com.example.talentbridge.chat.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationJpaRepository extends JpaRepository<ConversationJpaEntity, Long> {

    Optional<ConversationJpaEntity> findByResumeId(Long resumeId);

    Page<ConversationJpaEntity> findByCandidateIdOrCompanyId(Long candidateId, Long companyId, Pageable pageable);
}
