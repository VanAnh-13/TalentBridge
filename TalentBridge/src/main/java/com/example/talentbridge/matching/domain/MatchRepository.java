package com.example.talentbridge.matching.domain;

import java.util.List;
import java.util.Optional;

public interface MatchRepository {

    /** Stores the result; an earlier result for the same application is replaced. */
    void save(MatchResult result);

    Optional<MatchResult> findByResumeId(Long resumeId);

    /** The results of every scored application to the job, best score first. */
    List<MatchResult> findByJobId(Long jobId);
}
