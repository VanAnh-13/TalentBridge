package com.example.talentbridge.user.infrastructure.bootstrap;

import com.example.talentbridge.user.application.BootstrapAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * At startup: lock the old default admin if it still has its public password, then create the administrator
 * the operator asked for through {@code app.bootstrap.admin-email} / {@code app.bootstrap.admin-password}.
 */
@Component
@Order(2)
@RequiredArgsConstructor
public class AdminBootstrapRunner implements CommandLineRunner {

    private final BootstrapAdmin bootstrapAdmin;

    @Value("${app.bootstrap.admin-email:}")
    private String email;

    @Value("${app.bootstrap.admin-password:}")
    private String password;

    @Override
    public void run(String... args) {
        bootstrapAdmin.neutralizeLegacyDefaultAdmin(email, password);
        bootstrapAdmin.ensureAdmin(email, password);
    }
}
