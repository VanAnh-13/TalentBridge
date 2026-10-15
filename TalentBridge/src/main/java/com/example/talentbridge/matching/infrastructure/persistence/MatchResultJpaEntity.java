package com.example.talentbridge.matching.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "match_results")
@Getter
@Setter
@NoArgsConstructor
public class MatchResultJpaEntity {

    @Id
    @Column(name = "resume_id")
    private Long resumeId;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(nullable = false)
    private int score;

    /** JSON array of skill names. */
    @Column(name = "matched_skills", nullable = false, length = 4000)
    private String matchedSkills;

    /** JSON array of skill names. */
    @Column(name = "missing_skills", nullable = false, length = 4000)
    private String missingSkills;

    @Column(nullable = false, length = 1000)
    private String summary;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(name = "scored_at", nullable = false)
    private Instant scoredAt;
}
