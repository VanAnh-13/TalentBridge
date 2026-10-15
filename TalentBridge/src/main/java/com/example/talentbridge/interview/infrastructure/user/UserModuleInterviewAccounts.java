package com.example.talentbridge.interview.infrastructure.user;

import com.example.talentbridge.interview.application.port.Accounts;
import com.example.talentbridge.user.application.UserDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserModuleInterviewAccounts implements Accounts {

    private final UserDirectory users;

    @Override
    public Optional<Account> findByEmail(String email) {
        return users.findByEmail(email).map(u -> new Account(u.id(), u.name(), u.email(), u.companyId()));
    }
}
