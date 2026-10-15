package com.example.talentbridge.realtime.infrastructure.messaging;

import com.example.talentbridge.events.EventCodec;
import com.example.talentbridge.events.EventEnvelope;
import com.example.talentbridge.events.RealtimePush;
import com.example.talentbridge.events.Topics;
import com.example.talentbridge.realtime.application.PushToUser;
import com.example.talentbridge.shared.domain.exception.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Consumes {@link Topics#REALTIME} and hands each push to the user's open sessions. Live pushes are not stored, so
 * duplicates are dropped with a bounded memory of recent event ids.
 */
@Slf4j
@Component
public class RealtimePushListener {

    private static final int REMEMBERED = 10_000;

    private final PushToUser pushToUser;
    private final Map<String, Boolean> recent = new LinkedHashMap<>(256, 0.75f, false) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
            return size() > REMEMBERED;
        }
    };

    public RealtimePushListener(PushToUser pushToUser) {
        this.pushToUser = pushToUser;
    }

    @KafkaListener(id = "realtime-pushes", topics = Topics.REALTIME, groupId = "${app.events.group:notification-service}")
    public void on(String message) {
        try {
            EventEnvelope envelope = EventCodec.read(message);
            RealtimePush push = EventCodec.payload(envelope, RealtimePush.TYPE, RealtimePush.VERSION, RealtimePush.class);
            if (!firstTime(envelope.eventId())) return;
            pushToUser.execute(push.userId(), push.channel(), push.data());
        } catch (IllegalArgumentException | InvalidRequestException ex) {
            log.warn("Skipping an unusable realtime event: {}", ex.getMessage());
        }
    }

    private synchronized boolean firstTime(String eventId) {
        return recent.put(eventId, Boolean.TRUE) == null;
    }
}
