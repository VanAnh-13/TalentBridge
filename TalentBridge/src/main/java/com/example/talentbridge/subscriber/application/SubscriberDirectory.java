package com.example.talentbridge.subscriber.application;

import com.example.talentbridge.subscriber.domain.SubscribedSkill;
import com.example.talentbridge.subscriber.domain.Subscriber;
import com.example.talentbridge.subscriber.domain.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** The subscriber module's API for other modules (e.g. the daily job digest). */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscriberDirectory {

    /** Who to write to, and the names of the skills they follow. */
    public record Subscription(String email, List<String> skillNames) {
    }

    private final SubscriberRepository subscribers;

    public List<Subscription> all() {
        return subscribers.findAll().stream().map(SubscriberDirectory::toSubscription).toList();
    }

    public Optional<Subscription> findByEmail(String email) {
        return subscribers.findByEmail(email).map(SubscriberDirectory::toSubscription);
    }

    private static Subscription toSubscription(Subscriber s) {
        return new Subscription(s.getEmail(), s.getSkills().stream().map(SubscribedSkill::name).toList());
    }
}
