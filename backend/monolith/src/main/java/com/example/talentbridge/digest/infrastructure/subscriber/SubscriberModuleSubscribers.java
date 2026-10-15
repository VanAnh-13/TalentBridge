package com.example.talentbridge.digest.infrastructure.subscriber;

import com.example.talentbridge.digest.application.port.Subscribers;
import com.example.talentbridge.subscriber.application.SubscriberDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Adapts the subscriber module's API to the notification module's {@link Subscribers} port. */
@Component
@RequiredArgsConstructor
public class SubscriberModuleSubscribers implements Subscribers {

    private final SubscriberDirectory directory;

    @Override
    public List<Subscription> all() {
        return directory.all().stream().map(s -> new Subscription(s.email(), s.skillNames())).toList();
    }

    @Override
    public Optional<Subscription> findByEmail(String email) {
        return directory.findByEmail(email).map(s -> new Subscription(s.email(), s.skillNames()));
    }
}
