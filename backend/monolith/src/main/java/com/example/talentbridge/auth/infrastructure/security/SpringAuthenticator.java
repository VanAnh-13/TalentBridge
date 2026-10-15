package com.example.talentbridge.auth.infrastructure.security;

import com.example.talentbridge.auth.application.port.Authenticator;
import com.example.talentbridge.shared.domain.exception.AuthenticationFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringAuthenticator implements Authenticator {

    private final AuthenticationManager authenticationManager;

    @Override
    public void authenticate(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException();
        }
    }
}
