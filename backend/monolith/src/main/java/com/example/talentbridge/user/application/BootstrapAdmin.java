package com.example.talentbridge.user.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.user.application.port.PasswordHasher;
import com.example.talentbridge.user.application.port.RoleCatalog;
import com.example.talentbridge.user.domain.Gender;
import com.example.talentbridge.user.domain.User;
import com.example.talentbridge.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

/** First-run administration: no default admin is seeded, one is created only when the operator asks for it. */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BootstrapAdmin {

    static final String LEGACY_ADMIN_EMAIL = "admin@talentbridge.com";
    static final String LEGACY_ADMIN_PASSWORD = "admin123";

    private final UserRepository users;
    private final PasswordHasher hasher;
    private final RoleCatalog roles;

    /**
     * Older versions seeded admin@talentbridge.com with the public password "admin123". Databases created back
     * then still contain it, so rotate it to a random secret unless the operator re-provisions that very account.
     */
    public void neutralizeLegacyDefaultAdmin(String bootstrapEmail, String bootstrapPassword) {
        users.findByEmail(LEGACY_ADMIN_EMAIL).ifPresent(user -> {
            if (!hasher.matches(LEGACY_ADMIN_PASSWORD, user.getPasswordHash())) return;

            boolean reprovisioned = LEGACY_ADMIN_EMAIL.equalsIgnoreCase(bootstrapEmail)
                    && bootstrapPassword != null && !bootstrapPassword.isBlank();
            user.changePasswordHash(hasher.hash(reprovisioned ? bootstrapPassword : UUID.randomUUID().toString()));
            users.save(user);
            log.warn("Legacy default admin '{}' still used the public password; it was {}.", LEGACY_ADMIN_EMAIL,
                    reprovisioned ? "reset to BOOTSTRAP_ADMIN_PASSWORD"
                            : "locked with a random password (set BOOTSTRAP_ADMIN_EMAIL/PASSWORD to regain access)");
        });
    }

    /** Creates the administrator when both values are provided and the account does not exist yet. */
    public void ensureAdmin(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) return;
        if (users.existsByEmail(email)) return;

        Long adminRole = roles.idOfName("ADMIN").orElseThrow(() -> new NotFoundException("ADMIN role not found"));

        User admin = User.create(email, "Administrator", hasher.hash(password), LocalDate.of(1990, 1, 1), "System", Gender.OTHER);
        admin.assignRole(adminRole);
        users.save(admin);
        log.info("Bootstrap admin account created: {}", email);
    }
}
