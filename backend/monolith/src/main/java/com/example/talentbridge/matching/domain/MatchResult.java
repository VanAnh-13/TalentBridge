package com.example.talentbridge.matching.domain;

import com.example.talentbridge.shared.domain.exception.InvalidRequestException;

import java.time.Instant;
import java.util.List;

/** How well one application's CV fits the job it was sent to. Immutable: a new assessment replaces the old one. */
public class MatchResult {

    public static final int MAX_SUMMARY = 1000;

    private final Long resumeId;
    private final Long jobId;
    private final Long candidateId;
    private final int score;
    private final List<String> matchedSkills;
    private final List<String> missingSkills;
    private final String summary;
    private final String model;
    private final Instant scoredAt;

    private MatchResult(Long resumeId, Long jobId, Long candidateId, int score, List<String> matchedSkills,
                        List<String> missingSkills, String summary, String model, Instant scoredAt) {
        this.resumeId = resumeId;
        this.jobId = jobId;
        this.candidateId = candidateId;
        this.score = score;
        this.matchedSkills = List.copyOf(matchedSkills);
        this.missingSkills = List.copyOf(missingSkills);
        this.summary = summary;
        this.model = model;
        this.scoredAt = scoredAt;
    }

    /** A model may answer outside the range or ramble: the score is clamped to 0-100 and the summary shortened. */
    public static MatchResult assess(Long resumeId, Long jobId, Long candidateId, int score, List<String> matchedSkills,
                                     List<String> missingSkills, String summary, String model, Instant now) {
        if (resumeId == null || jobId == null || candidateId == null)
            throw new InvalidRequestException("Application details are missing");
        String text = summary == null ? "" : summary.strip();
        if (text.length() > MAX_SUMMARY) text = text.substring(0, MAX_SUMMARY - 1) + "…";
        return new MatchResult(resumeId, jobId, candidateId, Math.max(0, Math.min(100, score)),
                clean(matchedSkills), clean(missingSkills), text, model, now);
    }

    public static MatchResult restore(Long resumeId, Long jobId, Long candidateId, int score, List<String> matchedSkills,
                                      List<String> missingSkills, String summary, String model, Instant scoredAt) {
        return new MatchResult(resumeId, jobId, candidateId, score, matchedSkills, missingSkills, summary, model, scoredAt);
    }

    private static List<String> clean(List<String> skills) {
        return skills == null ? List.of() : skills.stream()
                .filter(s -> s != null && !s.isBlank()).map(String::strip).distinct().limit(30).toList();
    }

    public Long getResumeId() { return resumeId; }
    public Long getJobId() { return jobId; }
    public Long getCandidateId() { return candidateId; }
    public int getScore() { return score; }
    public List<String> getMatchedSkills() { return matchedSkills; }
    public List<String> getMissingSkills() { return missingSkills; }
    public String getSummary() { return summary; }
    public String getModel() { return model; }
    public Instant getScoredAt() { return scoredAt; }
}
