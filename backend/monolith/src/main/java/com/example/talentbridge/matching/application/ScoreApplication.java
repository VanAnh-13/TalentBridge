package com.example.talentbridge.matching.application;

import com.example.talentbridge.matching.application.port.Applications;
import com.example.talentbridge.matching.application.port.CvMatcher;
import com.example.talentbridge.matching.domain.MatchRepository;
import com.example.talentbridge.matching.domain.MatchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Optional;

/**
 * Judges one application and stores the result. It runs in the background without a caller, and deliberately
 * holds no database transaction while the matcher is working.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreApplication {

    private final Applications applications;
    private final CvMatcher matcher;
    private final MatchRepository results;
    private final Clock clock;

    /** @return the new result, or empty when the application, its CV or the matcher is not available. */
    public Optional<MatchResult> execute(Long resumeId) {
        if (!matcher.available()) return Optional.empty();
        try {
            Applications.Application application = applications.find(resumeId).orElse(null);
            byte[] cv = application == null ? null : applications.cvOf(resumeId).orElse(null);
            if (cv == null || cv.length == 0) return Optional.empty();

            CvMatcher.Verdict verdict = matcher.assess(new CvMatcher.Posting(application.jobName(), application.jobLevel(),
                    application.jobLocation(), application.jobDescription(), application.jobSkills()), cv);

            MatchResult result = MatchResult.assess(resumeId, application.jobId(), application.candidateId(),
                    verdict.score(), verdict.matchedSkills(), verdict.missingSkills(), verdict.summary(),
                    verdict.model(), clock.instant());
            results.save(result);
            return Optional.of(result);
        } catch (RuntimeException ex) {
            log.warn("Could not score application {}: {}", resumeId, ex.getMessage());
            return Optional.empty();
        }
    }
}
