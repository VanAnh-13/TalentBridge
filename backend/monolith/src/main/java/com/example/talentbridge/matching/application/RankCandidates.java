package com.example.talentbridge.matching.application;

import com.example.talentbridge.matching.application.port.Applications;
import com.example.talentbridge.matching.domain.MatchRepository;
import com.example.talentbridge.matching.domain.MatchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** The applicants of a job, best match first; applicants not scored yet come last. */
@Service
@RequiredArgsConstructor
public class RankCandidates {

    /** {@code match} is null for an applicant who has not been scored. */
    public record Ranked(Applications.Candidate candidate, MatchResult match) {
    }

    private final MatchAccess access;
    private final Applications applications;
    private final MatchRepository results;

    public List<Ranked> execute(Long jobId) {
        access.jobFor(jobId);
        Map<Long, MatchResult> byResume = results.findByJobId(jobId).stream()
                .collect(Collectors.toMap(MatchResult::getResumeId, Function.identity()));
        return applications.candidatesOf(jobId).stream()
                .map(c -> new Ranked(c, byResume.get(c.resumeId())))
                .sorted(Comparator
                        .comparing((Ranked r) -> r.match() == null)
                        .thenComparing(r -> r.match() == null ? 0 : -r.match().getScore())
                        .thenComparing(r -> r.candidate().resumeId()))
                .toList();
    }
}
