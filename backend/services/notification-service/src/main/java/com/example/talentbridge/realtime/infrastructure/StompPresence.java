package com.example.talentbridge.realtime.infrastructure;

import com.example.talentbridge.realtime.application.port.Presence;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

/** A user is online while at least one of their WebSocket sessions is connected to this server. */
@Component
@RequiredArgsConstructor
public class StompPresence implements Presence {

    private final SimpUserRegistry registry;

    @Override
    public boolean isOnline(Long userId) {
        return registry.getUser(String.valueOf(userId)) != null;
    }
}
