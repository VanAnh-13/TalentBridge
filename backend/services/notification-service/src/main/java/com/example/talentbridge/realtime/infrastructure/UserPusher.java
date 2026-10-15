package com.example.talentbridge.realtime.infrastructure;

import com.example.talentbridge.realtime.application.port.LivePusher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Sends to {@code /user/queue/<channel>} of one user, after the current transaction (if any) committed. */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserPusher implements LivePusher {

    private final SimpMessagingTemplate messaging;

    @Override
    public void push(long userId, String channel, Object payload) {
        Runnable send = () -> {
            try {
                messaging.convertAndSendToUser(String.valueOf(userId), "/queue/" + channel, payload);
            } catch (RuntimeException ex) {
                log.warn("Could not push to user {} on {}: {}", userId, channel, ex.getMessage());
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive())
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send.run();
                }
            });
        else
            send.run();
    }
}
