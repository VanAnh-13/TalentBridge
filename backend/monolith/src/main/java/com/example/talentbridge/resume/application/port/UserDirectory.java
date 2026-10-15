package com.example.talentbridge.resume.application.port;

import java.util.Optional;

/** Output port towards the user module. */
public interface UserDirectory {

    record User(Long id, String email, Long companyId, boolean admin) {
    }

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);
}
