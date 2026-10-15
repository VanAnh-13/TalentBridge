package com.example.talentbridge.auth.application;

import com.example.talentbridge.auth.application.port.Authenticator;
import com.example.talentbridge.auth.domain.DeviceInfo;
import com.example.talentbridge.user.application.GetUserAccount;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class Login {

    private final Authenticator authenticator;
    private final GetUserAccount getUserAccount;
    private final SessionIssuer sessions;

    public AuthSession execute(String email, String password, DeviceInfo device) {
        authenticator.authenticate(email, password);
        return sessions.open(getUserAccount.execute(email), device);
    }
}
