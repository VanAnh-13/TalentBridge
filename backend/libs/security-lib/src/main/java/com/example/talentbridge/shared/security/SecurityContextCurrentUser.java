package com.example.talentbridge.shared.security;

import com.example.talentbridge.shared.application.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Map;

/** {@link CurrentUser} backed by Spring Security's context (a JWT-authenticated request). */
@Component
public class SecurityContextCurrentUser implements CurrentUser {

    @Override
    public String email() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @Override
    public Long id() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) return null;
        return userIdOf(jwt);
    }

    /** The id in the token's {@code user} claim, or null. */
    public static Long userIdOf(Jwt jwt) {
        Object user = jwt.getClaim(TokenClaimNames.USER);
        Object id = user instanceof Map<?, ?> map ? map.get("id") : null;
        if (id instanceof Number n) return n.longValue();
        if (id instanceof String s && !s.isBlank()) {
            try {
                return Long.valueOf(s);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }
}
