package com.example.talentbridge.auth.infrastructure.persistence;

import com.example.talentbridge.auth.domain.PasswordResetToken;
import com.example.talentbridge.auth.domain.PasswordResetTokenRepository;
import com.example.talentbridge.user.infrastructure.persistence.UserJpaEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepository {

    private final PasswordResetTokenJpaRepository jpa;
    private final EntityManager entityManager;

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenJpaEntity entity = token.getId() == null
                ? new PasswordResetTokenJpaEntity()
                : jpa.findById(token.getId()).orElseThrow();

        entity.setTokenHash(token.getTokenHash());
        entity.setUser(entityManager.getReference(UserJpaEntity.class, token.getUserId()));
        entity.setExpiryDate(token.getExpiryDate());
        entity.setUsed(token.isUsed());
        return toDomain(jpa.saveAndFlush(entity));
    }

    @Override
    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return jpa.findByTokenHash(tokenHash).map(PasswordResetTokenRepositoryAdapter::toDomain);
    }

    @Override
    public long countRequestsByEmailSince(String email, Instant since) {
        return jpa.countRecentRequestsByEmail(email, since);
    }

    @Override
    public void invalidateAllFor(Long userId) {
        jpa.invalidateAllTokensForUser(userId);
    }

    private static PasswordResetToken toDomain(PasswordResetTokenJpaEntity e) {
        return PasswordResetToken.restore(
                e.getId(), e.getTokenHash(), e.getUser().getId(), e.getExpiryDate(), e.isUsed(), e.getCreatedAt());
    }
}
