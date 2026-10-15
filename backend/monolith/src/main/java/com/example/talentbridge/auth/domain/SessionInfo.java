package com.example.talentbridge.auth.domain;

import java.time.Instant;

/** One open login session of an account. */
public record SessionInfo(String sessionId, String deviceName, String deviceType, String userAgent,
                          Instant loginAt, boolean current) {
}
