package com.example.talentbridge.subscriber.application;

import com.example.talentbridge.shared.domain.exception.AlreadyExistsException;
import com.example.talentbridge.subscriber.application.port.SkillCatalog;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.subscriber.domain.Subscriber;
import com.example.talentbridge.subscriber.domain.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateSelfSubscriber {

    private final SubscriberRepository subscribers;
    private final SkillCatalog skillCatalog;
    private final CurrentUser currentUser;

    public Subscriber execute(List<Long> skillIds) {
        String email = currentUser.email();

        if (subscribers.findByEmail(email).isPresent())
            throw new AlreadyExistsException("This user is already subscribed");

        return subscribers.save(Subscriber.create(email, skillCatalog.findExisting(skillIds)));
    }
}
