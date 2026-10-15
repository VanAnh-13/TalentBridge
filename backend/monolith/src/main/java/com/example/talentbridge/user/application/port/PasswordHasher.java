package com.example.talentbridge.user.application.port;

/** Output port: password hashing. */
public interface PasswordHasher {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String hash);
}
