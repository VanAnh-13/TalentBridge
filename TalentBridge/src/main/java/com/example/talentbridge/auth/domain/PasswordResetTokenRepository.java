package com.example.talentbridge.auth.domain;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository {

    PasswordResetToken save(PasswordResetToken token);

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    long countRequestsByEmailSince(String email, Instant since);

    /** Marks every unused token of the user as used. */
    void invalidateAllFor(Long userId);
}
