package com.example.talentbridge.service;

import jakarta.mail.MessagingException;
import com.example.talentbridge.subscriber.infrastructure.persistence.SubscriberJpaEntity;

public interface EmailService {

    void sendJobNotificationForSubscriber(SubscriberJpaEntity subscriber) throws MessagingException;

    void sendJobNotificationManually(String email) throws MessagingException;
}
