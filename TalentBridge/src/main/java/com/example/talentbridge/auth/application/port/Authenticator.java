package com.example.talentbridge.auth.application.port;

/** Output port: checks an email / password pair. */
public interface Authenticator {

    /** @throws AuthenticationFailedException when the credentials are wrong */
    void authenticate(String email, String password);
}
