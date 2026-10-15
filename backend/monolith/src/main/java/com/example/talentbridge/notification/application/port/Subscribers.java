package com.example.talentbridge.notification.application.port;

import java.util.List;
import java.util.Optional;

/** Output port towards the subscriber module. */
public interface Subscribers {

    record Subscription(String email, List<String> skillNames) {
    }

    List<Subscription> all();

    Optional<Subscription> findByEmail(String email);
}
