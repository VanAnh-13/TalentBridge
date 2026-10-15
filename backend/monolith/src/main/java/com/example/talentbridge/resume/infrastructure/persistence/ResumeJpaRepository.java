package com.example.talentbridge.resume.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeJpaRepository extends
        JpaRepository<ResumeJpaEntity, Long>,
        JpaSpecificationExecutor<ResumeJpaEntity> {

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    Optional<ResumeJpaEntity> findByUserEmailAndJobId(String email, Long jobId);

    Optional<ResumeJpaEntity> findByUserEmailAndId(String email, Long id);

    List<ResumeJpaEntity> findByJobId(Long jobId);

    List<ResumeJpaEntity> findByUserId(Long userId);
}
