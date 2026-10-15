package com.example.talentbridge.access.infrastructure.bootstrap;

import com.example.talentbridge.access.infrastructure.persistence.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** The application cannot work without its built-in roles: refuse to start when one is missing. */
@Component
@Order(1)
@RequiredArgsConstructor
public class BuiltInRolesValidator implements CommandLineRunner {

    private final RoleJpaRepository roles;

    @Override
    public void run(String... args) {
        if (!roles.existsByName("ADMIN") || !roles.existsByName("USER") || !roles.existsByName("RECRUITER"))
            throw new IllegalStateException("Invalid Role table data");
    }
}
