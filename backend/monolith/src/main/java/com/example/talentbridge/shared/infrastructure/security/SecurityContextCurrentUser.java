package com.example.talentbridge.shared.infrastructure.security;

import com.example.talentbridge.shared.application.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** {@link CurrentUser} backed by Spring Security's context. */
@Component
public class SecurityContextCurrentUser implements CurrentUser {

    @Override
    public String email() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
