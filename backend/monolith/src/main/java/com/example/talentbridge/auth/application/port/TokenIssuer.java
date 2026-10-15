package com.example.talentbridge.auth.application.port;

import com.example.talentbridge.auth.application.TokenClaims;

import java.time.Duration;

/** Output port: signs access and refresh tokens. */
public interface TokenIssuer {

    /** An access token: who the user is and what they may do. */
    String issue(TokenClaims claims, Duration lifetime);

    /**
     * A refresh token: only whose session it is. It grants nothing by itself (the session store decides) and APIs
     * refuse it as a bearer token.
     */
    String issueRefresh(String subject, Duration lifetime);
}
