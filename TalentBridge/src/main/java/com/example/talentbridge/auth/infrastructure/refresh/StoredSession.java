package com.example.talentbridge.auth.infrastructure.refresh;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/** What is kept per open session (the same JSON shape that was stored before the migration). */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StoredSession {
    private String sessionId;
    private String deviceName;
    private String deviceType;
    private String userAgent;
    private Instant loginAt;
}
