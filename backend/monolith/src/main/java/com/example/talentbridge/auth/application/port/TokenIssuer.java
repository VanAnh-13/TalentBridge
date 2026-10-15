package com.example.talentbridge.auth.application.port;

import com.example.talentbridge.auth.application.TokenClaims;

import java.time.Duration;

/** Output port: signs access and refresh tokens. */
public interface TokenIssuer {

    String issue(TokenClaims claims, Duration lifetime);
}
