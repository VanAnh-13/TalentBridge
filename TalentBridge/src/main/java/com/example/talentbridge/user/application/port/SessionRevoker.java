package com.example.talentbridge.user.application.port;

/** Output port towards the auth module: ends sign-ins that are no longer trustworthy. */
public interface SessionRevoker {

    void revokeAll(Long userId);

    /** Revokes every session except the one that holds {@code keepRefreshToken} (may be null). */
    void revokeAllExcept(Long userId, String keepRefreshToken);
}
