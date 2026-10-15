package com.example.talentbridge.matching.infrastructure.user;

import com.example.talentbridge.matching.application.port.Accounts;
import com.example.talentbridge.user.application.UserDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserModuleMatchingAccounts implements Accounts {

    private final UserDirectory users;

    @Override
    public Optional<Account> findByEmail(String email) {
        return users.findByEmail(email)
                .map(u -> new Account(u.id(), u.email(), u.companyId(), "ADMIN".equals(u.roleName())));
    }
}
