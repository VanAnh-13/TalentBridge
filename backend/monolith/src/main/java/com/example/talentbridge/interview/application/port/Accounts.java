package com.example.talentbridge.interview.application.port;

import java.util.Optional;

/** Output port towards the user module. */
public interface Accounts {

    record Account(Long id, String name, String email, Long companyId) {
    }

    Optional<Account> findByEmail(String email);
}
