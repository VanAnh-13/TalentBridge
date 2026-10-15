package com.example.talentbridge.auth.application;

/** The outcome of a login or a refresh: who signed in and the two tokens. */
public record AuthSession(SessionUser user, String accessToken, String refreshToken) {
}
