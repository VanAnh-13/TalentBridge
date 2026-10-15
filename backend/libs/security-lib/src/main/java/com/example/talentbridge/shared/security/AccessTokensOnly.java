package com.example.talentbridge.shared.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/** APIs take access tokens only: a refresh token (signed by the same key) is refused as a bearer token. */
public class AccessTokensOnly implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error REFRESH_TOKEN = new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN,
            "A refresh token cannot be used to call an API", null);

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        return TokenClaimNames.REFRESH.equals(token.getClaimAsString(TokenClaimNames.TOKEN_USE))
                ? OAuth2TokenValidatorResult.failure(REFRESH_TOKEN)
                : OAuth2TokenValidatorResult.success();
    }
}
