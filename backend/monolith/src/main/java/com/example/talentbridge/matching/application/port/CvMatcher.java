package com.example.talentbridge.matching.application.port;

import java.util.List;

/** Output port: judges a CV against a job. The adapter decides how (a language model, rules, ...). */
public interface CvMatcher {

    record Posting(String title, String level, String location, String description, List<String> requiredSkills) {
    }

    record Verdict(int score, List<String> matchedSkills, List<String> missingSkills, String summary, String model) {
    }

    /** {@code false} when the capability is switched off (no credentials); callers then skip scoring. */
    boolean available();

    /** The model in use, for display. */
    String model();

    /** Fails with a {@link com.example.talentbridge.shared.domain.exception.ServiceUnavailableException} when the judge cannot answer. */
    Verdict assess(Posting job, byte[] cvPdf);
}
