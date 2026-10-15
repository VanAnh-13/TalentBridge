package com.example.talentbridge.init;

import com.example.talentbridge.model.Role;
import com.example.talentbridge.model.User;
import com.example.talentbridge.model.constant.Gender;
import com.example.talentbridge.repository.RoleRepository;
import com.example.talentbridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataValidator implements CommandLineRunner {

    private static final String LEGACY_ADMIN_EMAIL = "admin@talentbridge.com";
    private static final String LEGACY_ADMIN_PASSWORD = "admin123";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** Optional: when both are set and the account does not exist, an ADMIN user is created at startup. */
    @Value("${app.bootstrap.admin-email:}")
    private String bootstrapAdminEmail;

    @Value("${app.bootstrap.admin-password:}")
    private String bootstrapAdminPassword;

    @Override
    public void run(String... args) {
        checkRoleTable();
        neutralizeLegacyDefaultAdmin();
        bootstrapAdmin();
    }

    private void checkRoleTable() {
        if (
                !roleRepository.existsByName("ADMIN")
                        || !roleRepository.existsByName("USER")
                        || !roleRepository.existsByName("RECRUITER")
        )
            throw new IllegalStateException("Invalid Role table data");
    }

    /**
     * Older versions seeded admin@talentbridge.com with the public password "admin123" (data.sql).
     * Databases created back then still contain it, so rotate it to a random secret at startup
     * unless the operator explicitly re-provisions this account through the bootstrap properties.
     */
    private void neutralizeLegacyDefaultAdmin() {
        userRepository.findByEmail(LEGACY_ADMIN_EMAIL).ifPresent(user -> {
            if (!passwordEncoder.matches(LEGACY_ADMIN_PASSWORD, user.getPassword())) return;

            boolean reprovisioned = LEGACY_ADMIN_EMAIL.equalsIgnoreCase(bootstrapAdminEmail)
                    && !bootstrapAdminPassword.isBlank();
            String newPassword = reprovisioned ? bootstrapAdminPassword : UUID.randomUUID().toString();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            log.warn("Legacy default admin '{}' still used the public password; it was {}.",
                    LEGACY_ADMIN_EMAIL,
                    reprovisioned ? "reset to BOOTSTRAP_ADMIN_PASSWORD" : "locked with a random password (set BOOTSTRAP_ADMIN_EMAIL/PASSWORD to regain access)");
        });
    }

    private void bootstrapAdmin() {
        if (bootstrapAdminEmail.isBlank() || bootstrapAdminPassword.isBlank()) return;
        if (userRepository.existsByEmail(bootstrapAdminEmail)) return;

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

        User admin = new User();
        admin.setEmail(bootstrapAdminEmail);
        admin.setName("Administrator");
        admin.setPassword(passwordEncoder.encode(bootstrapAdminPassword));
        admin.setDob(LocalDate.of(1990, 1, 1));
        admin.setAddress("System");
        admin.setGender(Gender.OTHER);
        admin.setRole(adminRole);
        userRepository.save(admin);
        log.info("Bootstrap admin account created: {}", bootstrapAdminEmail);
    }
}
