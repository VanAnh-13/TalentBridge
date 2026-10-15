package com.example.talentbridge.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PasswordResetTokenJpaRepository extends JpaRepository<PasswordResetTokenJpaEntity, Long> {

    Optional<PasswordResetTokenJpaEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE PasswordResetTokenJpaEntity p SET p.used = true WHERE p.user.id = :userId AND p.used = false")
    void invalidateAllTokensForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM PasswordResetTokenJpaEntity p WHERE p.user.email = :email AND p.createdAt > :since")
    long countRecentRequestsByEmail(@Param("email") String email, @Param("since") Instant since);
}
