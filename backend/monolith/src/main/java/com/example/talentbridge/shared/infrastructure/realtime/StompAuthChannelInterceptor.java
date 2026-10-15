package com.example.talentbridge.shared.infrastructure.realtime;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

/**
 * Authenticates a STOMP session from the access token in the CONNECT frame ("Authorization: Bearer ...").
 * The principal's name is the user id, so servers address people with {@code convertAndSendToUser(userId, ...)}.
 * Subscriptions are limited to the caller's own {@code /user/queue/*} destinations.
 */
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    /** The user id as the principal name. */
    record UserPrincipal(String name) implements Principal {
        @Override
        public String getName() {
            return name;
        }
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null)
            return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            accessor.setUser(authenticate(accessor.getFirstNativeHeader("Authorization")));
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            if (accessor.getUser() == null || destination == null || !destination.startsWith("/user/queue/"))
                throw new MessagingException("Subscription refused");
        } else if (StompCommand.SEND.equals(accessor.getCommand()) && accessor.getUser() == null) {
            throw new MessagingException("Not authenticated");
        }
        return message;
    }

    private Principal authenticate(String header) {
        if (header == null || !header.startsWith("Bearer "))
            throw new MessagingException("Missing access token");
        try {
            Jwt jwt = jwtDecoder.decode(header.substring(7));
            Object user = jwt.getClaim("user");
            Object id = user instanceof Map<?, ?> map ? map.get("id") : null;
            if (id == null)
                throw new MessagingException("Invalid access token");
            return new UserPrincipal(String.valueOf(id));
        } catch (JwtException ex) {
            throw new MessagingException("Invalid access token");
        }
    }
}
