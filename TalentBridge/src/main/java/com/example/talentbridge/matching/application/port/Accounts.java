package com.example.talentbridge.matching.application.port;

import java.util.Optional;

/** Output port towards the user module. */
public interface Accounts {

    record Account(Long id, String email, Long companyId, boolean admin) {
    }

    Optional<Account> findByEmail(String email);
}
