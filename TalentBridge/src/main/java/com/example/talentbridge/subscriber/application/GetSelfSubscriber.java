package com.example.talentbridge.subscriber.application;

import java.util.Optional;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.subscriber.domain.Subscriber;
import com.example.talentbridge.subscriber.domain.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSelfSubscriber {

    private final SubscriberRepository subscribers;
    private final CurrentUser currentUser;

    /** The caller's subscription, or empty when they have not subscribed. */
    public Optional<Subscriber> execute() {
        return subscribers.findByEmail(currentUser.email());
    }
}
