package com.example.talentbridge.subscriber.domain;

import java.util.List;
import java.util.Optional;

public interface SubscriberRepository {

    Subscriber save(Subscriber subscriber);

    Optional<Subscriber> findByEmail(String email);

    List<Subscriber> findAll();

    void delete(Subscriber subscriber);
}
