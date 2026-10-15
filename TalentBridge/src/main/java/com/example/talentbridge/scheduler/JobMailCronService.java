package com.example.talentbridge.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.talentbridge.subscriber.infrastructure.persistence.SubscriberJpaEntity;
import com.example.talentbridge.subscriber.infrastructure.persistence.SubscriberJpaRepository;
import com.example.talentbridge.service.EmailService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "spring.mail.host",
        matchIfMissing = false
)
public class JobMailCronService {

    private final SubscriberJpaRepository subscriberRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendJobRecommendationToAllUsers() {
        List<SubscriberJpaEntity> subscribers = subscriberRepository.findAll();
        int sent = 0, failed = 0;

        for (SubscriberJpaEntity subscriber : subscribers) {
            try {
                emailService.sendJobNotificationForSubscriber(subscriber);
                sent++;
            } catch (Exception ex) {
                failed++;
            }
        }

        log.info("Sent job mail to {} users, failed {}", sent, failed);
    }
}
