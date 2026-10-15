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

@Slf4j
@Component
@RequiredArgsConstructor
public class DataValidator implements CommandLineRunner {

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
