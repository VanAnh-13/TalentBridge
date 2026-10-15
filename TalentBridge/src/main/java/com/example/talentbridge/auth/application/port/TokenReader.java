package com.example.talentbridge.auth.application.port;

/** Output port: verifies a signed token and reads its subject. */
public interface TokenReader {

    /** The subject (login email) of a valid token. @throws InvalidTokenException when it is malformed or expired */
    String subjectOf(String token);
}
