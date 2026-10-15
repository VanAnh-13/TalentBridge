package com.example.talentbridge.auth.presentation;

import com.example.talentbridge.auth.domain.SessionInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SessionResponse {

    private String sessionId;
    private String deviceName;
    private String deviceType;
    private String userAgent;
    private Instant loginAt;
    private boolean current;

    public static SessionResponse from(SessionInfo s) {
        return new SessionResponse(s.sessionId(), s.deviceName(), s.deviceType(), s.userAgent(), s.loginAt(), s.current());
    }
}
