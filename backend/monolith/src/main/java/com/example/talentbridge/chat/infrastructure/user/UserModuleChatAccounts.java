package com.example.talentbridge.chat.infrastructure.user;

import com.example.talentbridge.chat.application.port.Accounts;
import com.example.talentbridge.user.application.UserDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserModuleChatAccounts implements Accounts {

    private final UserDirectory users;

    @Override
    public Optional<Account> findByEmail(String email) {
        return users.findByEmail(email).map(UserModuleChatAccounts::account);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return users.findById(id).map(UserModuleChatAccounts::account);
    }

    @Override
    public List<Account> membersOfCompany(Long companyId) {
        return users.findByCompanyId(companyId).stream().map(UserModuleChatAccounts::account).toList();
    }

    private static Account account(UserDirectory.UserSummary u) {
        return new Account(u.id(), u.name(), u.email(), u.companyId());
    }
}
