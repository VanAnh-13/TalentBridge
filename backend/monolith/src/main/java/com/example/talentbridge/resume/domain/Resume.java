package com.example.talentbridge.resume.domain;

import java.time.Instant;

/** A candidate's application (CV) to a job. The candidate and the job are referenced by id. */
public class Resume {

    private final Long id;
    private final String email;
    private ResumeStatus status;
    private String fileKey;
    private long version;
    private final Long userId;
    private final Long jobId;
    private final Instant createdAt;
    private final String createdBy;
    private final Instant updatedAt;

    private Resume(Long id, String email, ResumeStatus status, String fileKey, long version, Long userId, Long jobId,
                   Instant createdAt, String createdBy, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.status = status;
        this.fileKey = fileKey;
        this.version = version;
        this.userId = userId;
        this.jobId = jobId;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
    }

    /** A new application always starts as {@link ResumeStatus#PENDING}: only the employer moves it forward. */
    public static Resume submit(String email, Long userId, Long jobId) {
        return new Resume(null, email, ResumeStatus.PENDING, null, 1L, userId, jobId, null, null, null);
    }

    public static Resume restore(Long id, String email, ResumeStatus status, String fileKey, long version,
                                 Long userId, Long jobId, Instant createdAt, String createdBy, Instant updatedAt) {
        return new Resume(id, email, status, fileKey, version, userId, jobId, createdAt, createdBy, updatedAt);
    }

    public void attachFile(String key) {
        this.fileKey = key;
    }

    /** A new upload replaces the file and bumps the version, which is part of the storage key. */
    public void replaceFile(String newKey) {
        this.version = version + 1;
        this.fileKey = newKey;
    }

    public long nextVersion() {
        return version + 1;
    }

    public void changeStatus(ResumeStatus newStatus) {
        this.status = newStatus;
    }

    public boolean isOwnedBy(Long candidateId) {
        return userId != null && userId.equals(candidateId);
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public ResumeStatus getStatus() { return status; }
    public String getFileKey() { return fileKey; }
    public long getVersion() { return version; }
    public Long getUserId() { return userId; }
    public Long getJobId() { return jobId; }
    public Instant getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    public Instant getUpdatedAt() { return updatedAt; }
}
