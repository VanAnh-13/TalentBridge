package com.example.talentbridge.chat.domain;

import java.time.Instant;

/**
 * The thread between a candidate and the recruiters of the company they applied to, about one application.
 * Everybody on the company side shares the thread.
 */
public class Conversation {

    private final Long id;
    private final Long resumeId;
    private final Long companyId;
    private final Long candidateId;
    private final Instant createdAt;
    private Instant lastMessageAt;

    private Conversation(Long id, Long resumeId, Long companyId, Long candidateId, Instant createdAt, Instant lastMessageAt) {
        this.id = id;
        this.resumeId = resumeId;
        this.companyId = companyId;
        this.candidateId = candidateId;
        this.createdAt = createdAt;
        this.lastMessageAt = lastMessageAt;
    }

    public static Conversation open(Long resumeId, Long companyId, Long candidateId, Instant now) {
        return new Conversation(null, resumeId, companyId, candidateId, now, now);
    }

    public static Conversation restore(Long id, Long resumeId, Long companyId, Long candidateId, Instant createdAt,
                                       Instant lastMessageAt) {
        return new Conversation(id, resumeId, companyId, candidateId, createdAt, lastMessageAt);
    }

    public boolean isCandidate(Long userId) {
        return candidateId.equals(userId);
    }

    /** A person is on the company side when they work for the company that received the application. */
    public boolean isCompanyMember(Long userCompanyId) {
        return userCompanyId != null && userCompanyId.equals(companyId);
    }

    public boolean hasParticipant(Long userId, Long userCompanyId) {
        return isCandidate(userId) || isCompanyMember(userCompanyId);
    }

    public void spokeAt(Instant when) {
        lastMessageAt = when;
    }

    public Long getId() { return id; }
    public Long getResumeId() { return resumeId; }
    public Long getCompanyId() { return companyId; }
    public Long getCandidateId() { return candidateId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastMessageAt() { return lastMessageAt; }
}
