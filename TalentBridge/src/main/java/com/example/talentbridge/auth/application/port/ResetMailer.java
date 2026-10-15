package com.example.talentbridge.auth.application.port;

/** Output port: delivers the password-reset link to the account owner. */
public interface ResetMailer {

    void sendPasswordReset(String email, String name, String rawToken);
}
