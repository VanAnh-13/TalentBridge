package com.example.talentbridge.matching.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchResultJpaRepository extends JpaRepository<MatchResultJpaEntity, Long> {

    List<MatchResultJpaEntity> findByJobIdOrderByScoreDescScoredAtAsc(Long jobId);
}
