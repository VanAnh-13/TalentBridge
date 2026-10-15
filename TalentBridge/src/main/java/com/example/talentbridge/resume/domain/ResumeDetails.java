package com.example.talentbridge.resume.domain;

import java.util.List;

/** Read model: a resume with the display data of the candidate, the job and its company. */
public record ResumeDetails(Resume resume, UserSummary user, JobSummary job, CompanySummary company) {

    public record UserSummary(Long id, String email) {
    }

    public record JobSummary(Long id, String name, String location, List<String> skills, String level, String description) {
    }

    public record CompanySummary(Long id, String name, String logoUrl) {
    }

    /** The company that received the application, or {@code null} when the job has none. */
    public Long companyId() {
        return company == null ? null : company.id();
    }
}
