package com.example.talentbridge.interview.domain;

import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface InterviewRepository {

    Interview save(Interview interview);

    Optional<Interview> findById(Long id);

    /** True while a proposed or confirmed interview exists for the application. */
    boolean existsActiveForResume(Long resumeId);

    /** Newest first. */
    PageResult<Interview> findByCandidate(Long candidateId, PageQuery page);

    PageResult<Interview> findByCompany(Long companyId, PageQuery page);

    /** Confirmed interviews that start in {@code (from, to]}. */
    List<Interview> findConfirmedStartingBetween(Instant from, Instant to);
}
