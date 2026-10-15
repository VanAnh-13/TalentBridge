package com.example.talentbridge.subscriber.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.subscriber.domain.Subscriber;
import com.example.talentbridge.subscriber.domain.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteSelfSubscriber {

    private final SubscriberRepository subscribers;
    private final CurrentUser currentUser;

    public void execute() {
        Subscriber subscriber = subscribers.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đăng ký người dùng này"));

        subscribers.delete(subscriber);
    }
}
