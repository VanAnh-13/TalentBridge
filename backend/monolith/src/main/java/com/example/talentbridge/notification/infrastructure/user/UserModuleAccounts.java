package com.example.talentbridge.notification.infrastructure.user;

import com.example.talentbridge.notification.application.port.Accounts;
import com.example.talentbridge.user.application.UserDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserModuleAccounts implements Accounts {

    private final UserDirectory users;

    @Override
    public Optional<Long> idOfEmail(String email) {
        return users.findByEmail(email).map(UserDirectory.UserSummary::id);
    }
}
