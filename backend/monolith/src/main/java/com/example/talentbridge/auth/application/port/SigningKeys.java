package com.example.talentbridge.auth.application.port;

import java.util.Map;

/** Output port: the public half of the keys tokens are signed with, as a JSON Web Key Set (RFC 7517). */
public interface SigningKeys {

    Map<String, Object> publicJwkSet();
}
