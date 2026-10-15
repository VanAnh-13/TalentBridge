package com.example.talentbridge.job.domain;

import java.util.List;

/** Read model: a job together with the display data of its company and skills. */
public record JobDetails(Job job, CompanySummary company, List<SkillSummary> skills) {

    public record CompanySummary(Long id, String name, String logoUrl, String address) {
    }

    public record SkillSummary(Long id, String name) {
    }
}
