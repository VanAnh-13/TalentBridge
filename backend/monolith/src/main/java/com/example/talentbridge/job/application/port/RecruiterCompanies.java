package com.example.talentbridge.job.application.port;

import java.util.Optional;

/** Output port towards the user module: which company does a user work for. */
public interface RecruiterCompanies {

    record UserCompany(Long userId, Long companyId) {
    }

    Optional<UserCompany> findByEmail(String email);
}
