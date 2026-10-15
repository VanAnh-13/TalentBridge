package com.example.talentbridge.matching.presentation;

import com.example.talentbridge.matching.application.MatchingStatus;
import com.example.talentbridge.matching.application.RankCandidates;
import com.example.talentbridge.matching.application.RecommendJobsFor;
import com.example.talentbridge.matching.domain.MatchResult;

import java.time.Instant;
import java.util.List;

public final class MatchResponses {

    private MatchResponses() {
    }

    public record MatchResponse(Long resumeId, Long jobId, int score, List<String> matchedSkills,
                                List<String> missingSkills, String summary, String model, Instant scoredAt) {
        static MatchResponse from(MatchResult r) {
            return r == null ? null : new MatchResponse(r.getResumeId(), r.getJobId(), r.getScore(), r.getMatchedSkills(),
                    r.getMissingSkills(), r.getSummary(), r.getModel(), r.getScoredAt());
        }
    }

    /** {@code match} is null while the application has not been scored. */
    public record MatchLookupResponse(boolean scored, MatchResponse match) {
    }

    public record RankedCandidateResponse(Long resumeId, Long candidateId, String email, String status,
                                          MatchResponse match) {
        static RankedCandidateResponse from(RankCandidates.Ranked r) {
            return new RankedCandidateResponse(r.candidate().resumeId(), r.candidate().candidateId(),
                    r.candidate().email(), r.candidate().status(), MatchResponse.from(r.match()));
        }
    }

    public record RecommendationResponse(Long jobId, String name, String companyName, String location,
                                         List<String> skills, int fitPercent, List<String> sharedSkills) {
        static RecommendationResponse from(RecommendJobsFor.Recommendation r) {
            return new RecommendationResponse(r.job().id(), r.job().name(), r.job().companyName(), r.job().location(),
                    r.job().skills(), r.fitPercent(), r.sharedSkills());
        }
    }

    public record StatusResponse(boolean enabled, String model) {
        static StatusResponse from(MatchingStatus.Status s) {
            return new StatusResponse(s.enabled(), s.model());
        }
    }
}
