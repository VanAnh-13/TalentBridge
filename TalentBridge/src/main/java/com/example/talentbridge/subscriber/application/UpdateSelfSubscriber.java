package com.example.talentbridge.subscriber.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
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
public class UpdateSelfSubscriber {

    private final SubscriberRepository subscribers;
    private final SkillCatalog skillCatalog;
    private final CurrentUser currentUser;

    public Subscriber execute(List<Long> skillIds) {
        Subscriber subscriber = subscribers.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đăng ký người dùng này"));

        subscriber.changeSkills(skillCatalog.findExisting(skillIds));
        return subscribers.save(subscriber);
    }
}
