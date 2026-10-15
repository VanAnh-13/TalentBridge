package com.example.talentbridge.notification.application.port;

import java.util.Optional;

/** Output port towards the user module. */
public interface Accounts {

    Optional<Long> idOfEmail(String email);
}
