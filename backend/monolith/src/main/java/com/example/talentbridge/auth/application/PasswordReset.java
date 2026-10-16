package com.example.talentbridge.auth.application;

import com.example.talentbridge.shared.domain.exception.InvalidRequestException;
import com.example.talentbridge.auth.application.port.ResetMailer;
import com.example.talentbridge.auth.domain.PasswordPolicy;
import com.example.talentbridge.auth.domain.PasswordResetToken;
import com.example.talentbridge.auth.domain.PasswordResetTokenRepository;
import com.example.talentbridge.auth.domain.ResetTokens;
import com.example.talentbridge.user.application.ResetUserPassword;
import com.example.talentbridge.user.application.UserDirectory;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PasswordReset {

    private static final Duration RATE_LIMIT = Duration.ofMinutes(1);

    private final PasswordResetTokenRepository tokens;
    private final UserDirectory users;
    private final ResetUserPassword resetUserPassword;
    private final RevokeSessions revokeSessions;
    private final ResetMailer mailer;

    /** Outcome of checking a reset link. */
    public record TokenCheck(boolean valid, String message, String errorCode) {
        static TokenCheck ok() { return new TokenCheck(true, "Token is valid", null); }
        static TokenCheck notFound() { return new TokenCheck(false, "Invalid link", "TOKEN_INVALID"); }
        static TokenCheck used() { return new TokenCheck(false, "This link has already been used", "TOKEN_USED"); }
        static TokenCheck expired() { return new TokenCheck(false, "This link has expired. Please request a new one", "TOKEN_EXPIRED"); }
    }

    /** Sends a reset link. Never reveals whether the email belongs to an account, nor rate limiting. */
    public void request(String email) {
        if (tokens.countRequestsByEmailSince(email, Instant.now().minus(RATE_LIMIT)) > 0)
            return;

        Optional<UserDirectory.UserSummary> user = users.findByEmail(email);
        if (user.isEmpty())
            return;

        String rawToken = ResetTokens.generate();
        tokens.save(PasswordResetToken.issue(ResetTokens.hash(rawToken), user.get().id(), ResetTokens.expiryFrom(Instant.now())));
        mailer.sendPasswordReset(user.get().email(), user.get().name(), rawToken);
    }

    @Transactional(readOnly = true)
    public TokenCheck verify(String rawToken) {
        Optional<PasswordResetToken> token = tokens.findByTokenHash(ResetTokens.hash(rawToken));
        if (token.isEmpty()) return TokenCheck.notFound();
        if (token.get().isUsed()) return TokenCheck.used();
        if (token.get().isExpired(Instant.now())) return TokenCheck.expired();
        return TokenCheck.ok();
    }

    /** Consumes the link and sets the new password; every other pending link of the account is revoked. */
    public void reset(String rawToken, String newPassword, String confirmation) {
        var violations = PasswordPolicy.violations(newPassword);
        if (!violations.isEmpty())
            throw new InvalidRequestException(String.join(", ", violations));
        if (!PasswordPolicy.confirms(newPassword, confirmation))
            throw new InvalidRequestException("The password confirmation does not match");

        PasswordResetToken token = tokens.findByTokenHash(ResetTokens.hash(rawToken))
                .orElseThrow(() -> new InvalidRequestException("Invalid link"));
        if (token.isUsed())
            throw new InvalidRequestException("This link has already been used");
        if (token.isExpired(Instant.now()))
            throw new InvalidRequestException("This link has expired. Please request a new one");

        resetUserPassword.execute(token.getUserId(), newPassword);
        tokens.invalidateAllFor(token.getUserId());
        revokeSessions.all(token.getUserId());
    }
}
