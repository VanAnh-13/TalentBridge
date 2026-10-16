package com.example.talentbridge.matching.application;

import com.example.talentbridge.matching.application.port.CvMatcher;
import com.example.talentbridge.matching.application.port.ScoringQueue;
import com.example.talentbridge.matching.domain.MatchRepository;
import com.example.talentbridge.matching.domain.MatchResult;
import com.example.talentbridge.shared.domain.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Reads, or asks again for, the AI assessment of one application. */
@Service
@RequiredArgsConstructor
public class MatchOfApplication {

    private final MatchAccess access;
    private final MatchRepository results;
    private final CvMatcher matcher;
    private final ScoringQueue queue;

    /** The stored assessment; empty when the application has not been scored (yet). */
    public Optional<MatchResult> find(Long resumeId) {
        access.applicationFor(resumeId);
        return results.findByResumeId(resumeId);
    }

    /** Queues a fresh assessment; the answer arrives later and shows up in {@link #find}. */
    public void recompute(Long resumeId) {
        access.applicationFor(resumeId);
        if (!matcher.available())
            throw new ServiceUnavailableException("AI scoring is not enabled");
        queue.enqueue(resumeId);
    }
}
