package com.example.talentbridge.interview.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface InterviewJpaRepository extends JpaRepository<InterviewJpaEntity, Long> {

    boolean existsByResumeIdAndStatusIn(Long resumeId, Collection<String> statuses);

    Page<InterviewJpaEntity> findByCandidateId(Long candidateId, Pageable pageable);

    Page<InterviewJpaEntity> findByCompanyId(Long companyId, Pageable pageable);

    @Query("select i from InterviewJpaEntity i where i.status = 'CONFIRMED' and i.confirmedSlot > :from and i.confirmedSlot <= :to")
    List<InterviewJpaEntity> findConfirmedStartingBetween(@Param("from") Instant from, @Param("to") Instant to);
}
