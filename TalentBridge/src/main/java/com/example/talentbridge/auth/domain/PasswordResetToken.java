package com.example.talentbridge.auth.domain;

import java.time.Instant;

/** A single-use, short-lived proof that someone controls the mailbox of an account. Only its hash is stored. */
public class PasswordResetToken {

    private final Long id;
    private final String tokenHash;
    private final Long userId;
    private final Instant expiryDate;
    private boolean used;
    private final Instant createdAt;

    private PasswordResetToken(Long id, String tokenHash, Long userId, Instant expiryDate, boolean used, Instant createdAt) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.used = used;
        this.createdAt = createdAt;
    }

    public static PasswordResetToken issue(String tokenHash, Long userId, Instant expiryDate) {
        return new PasswordResetToken(null, tokenHash, userId, expiryDate, false, null);
    }

    public static PasswordResetToken restore(Long id, String tokenHash, Long userId, Instant expiryDate,
                                             boolean used, Instant createdAt) {
        return new PasswordResetToken(id, tokenHash, userId, expiryDate, used, createdAt);
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiryDate);
    }

    public void markUsed() {
        this.used = true;
    }

    public Long getId() { return id; }
    public String getTokenHash() { return tokenHash; }
    public Long getUserId() { return userId; }
    public Instant getExpiryDate() { return expiryDate; }
    public boolean isUsed() { return used; }
    public Instant getCreatedAt() { return createdAt; }
}
