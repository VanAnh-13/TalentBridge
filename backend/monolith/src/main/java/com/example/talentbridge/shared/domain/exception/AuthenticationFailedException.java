package com.example.talentbridge.shared.domain.exception;

/** Login failed: unknown account or wrong password (HTTP 401). */
public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException() {
        super("Invalid login credentials");
    }
}
