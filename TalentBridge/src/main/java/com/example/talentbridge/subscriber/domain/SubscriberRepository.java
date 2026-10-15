package com.example.talentbridge.subscriber.domain;

import java.util.Optional;

public interface SubscriberRepository {

    Subscriber save(Subscriber subscriber);

    Optional<Subscriber> findByEmail(String email);

    void delete(Subscriber subscriber);
}
