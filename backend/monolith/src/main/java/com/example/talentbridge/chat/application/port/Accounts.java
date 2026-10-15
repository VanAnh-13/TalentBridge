package com.example.talentbridge.chat.application.port;

import java.util.List;
import java.util.Optional;

/** Output port towards the user module. */
public interface Accounts {

    record Account(Long id, String name, String email, Long companyId) {
    }

    Optional<Account> findByEmail(String email);

    Optional<Account> findById(Long id);

    List<Account> membersOfCompany(Long companyId);
}
