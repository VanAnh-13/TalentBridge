package com.example.talentbridge.auth.application;

import com.example.talentbridge.auth.application.port.SigningKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/** Other services verify our access tokens with these public keys; nothing secret is published. */
@Service
@RequiredArgsConstructor
public class PublishSigningKeys {

    private final SigningKeys keys;

    public Map<String, Object> execute() {
        return keys.publicJwkSet();
    }
}
