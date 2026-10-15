package com.example.talentbridge.matching.infrastructure.persistence;

import com.example.talentbridge.matching.domain.MatchRepository;
import com.example.talentbridge.matching.domain.MatchResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class MatchRepositoryAdapter implements MatchRepository {

    private static final int COLUMN = 4000;
    private static final TypeReference<List<String>> NAMES = new TypeReference<>() { };

    private final MatchResultJpaRepository jpa;
    private final ObjectMapper json;

    /** Own transaction: scoring may be triggered from the afterCommit hook of the submitting transaction. */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(MatchResult r) {
        MatchResultJpaEntity e = jpa.findById(r.getResumeId()).orElseGet(MatchResultJpaEntity::new);
        e.setResumeId(r.getResumeId());
        e.setJobId(r.getJobId());
        e.setCandidateId(r.getCandidateId());
        e.setScore(r.getScore());
        e.setMatchedSkills(write(r.getMatchedSkills()));
        e.setMissingSkills(write(r.getMissingSkills()));
        e.setSummary(r.getSummary());
        e.setModel(r.getModel());
        e.setScoredAt(r.getScoredAt());
        jpa.saveAndFlush(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MatchResult> findByResumeId(Long resumeId) {
        return jpa.findById(resumeId).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResult> findByJobId(Long jobId) {
        return jpa.findByJobIdOrderByScoreDescScoredAtAsc(jobId).stream().map(this::toDomain).toList();
    }

    /** The column is bounded: drop trailing names rather than fail the whole result. */
    private String write(List<String> names) {
        List<String> kept = new java.util.ArrayList<>(names);
        while (true) {
            try {
                String text = json.writeValueAsString(kept);
                if (text.length() <= COLUMN || kept.isEmpty()) return text.length() <= COLUMN ? text : "[]";
                kept.remove(kept.size() - 1);
            } catch (JsonProcessingException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    private List<String> read(String text) {
        try {
            return json.readValue(text, NAMES);
        } catch (JsonProcessingException ex) {
            return List.of();
        }
    }

    private MatchResult toDomain(MatchResultJpaEntity e) {
        return MatchResult.restore(e.getResumeId(), e.getJobId(), e.getCandidateId(), e.getScore(),
                read(e.getMatchedSkills()), read(e.getMissingSkills()), e.getSummary(), e.getModel(), e.getScoredAt());
    }
}
