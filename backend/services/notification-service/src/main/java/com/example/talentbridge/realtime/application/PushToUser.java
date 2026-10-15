package com.example.talentbridge.realtime.application;

import com.example.talentbridge.realtime.application.port.LivePusher;
import com.example.talentbridge.shared.domain.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/** Live delivery to one user's browser tabs; nothing is stored, a user who is not connected misses it. */
@Service
@RequiredArgsConstructor
public class PushToUser {

    /** Channel names become part of a STOMP destination, so only plain names are accepted. */
    private static final Pattern CHANNEL = Pattern.compile("[a-z][a-z0-9-]{0,39}");

    private final LivePusher pusher;

    public void execute(long userId, String channel, Object payload) {
        if (channel == null || !CHANNEL.matcher(channel).matches())
            throw new InvalidRequestException("Invalid channel: " + channel);
        pusher.push(userId, channel, payload);
    }
}
