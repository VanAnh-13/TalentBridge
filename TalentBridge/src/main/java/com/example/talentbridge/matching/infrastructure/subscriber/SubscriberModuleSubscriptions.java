package com.example.talentbridge.matching.infrastructure.subscriber;

import com.example.talentbridge.matching.application.port.Subscriptions;
import com.example.talentbridge.subscriber.application.SubscriberDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriberModuleSubscriptions implements Subscriptions {

    private final SubscriberDirectory subscribers;

    @Override
    public List<String> skillsOf(String email) {
        return subscribers.findByEmail(email).map(SubscriberDirectory.Subscription::skillNames).orElse(List.of());
    }
}
