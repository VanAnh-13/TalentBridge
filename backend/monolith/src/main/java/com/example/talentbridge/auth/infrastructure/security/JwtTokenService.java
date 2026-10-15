package com.example.talentbridge.auth.infrastructure.security;

import com.example.talentbridge.auth.application.TokenClaims;
import com.example.talentbridge.auth.application.port.TokenIssuer;
import com.example.talentbridge.auth.application.port.TokenReader;
import com.example.talentbridge.shared.domain.exception.InvalidTokenException;
import com.example.talentbridge.shared.security.TokenClaimNames;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Signs (RS256) and reads the JWTs used as access and refresh tokens. Access tokens say who the user is and what they
 * may do; refresh tokens only whose session it is, so they stay small enough for a cookie and grant nothing alone.
 */
@Component
public class JwtTokenService implements TokenIssuer, TokenReader {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder refreshTokenDecoder;
    private final RsaSigningKey key;
    private final String issuer;

    public JwtTokenService(JwtEncoder jwtEncoder, RsaSigningKey key,
                           @Value("${jwt.issuer:" + TokenClaimNames.DEFAULT_ISSUER + "}") String issuer) {
        this.jwtEncoder = jwtEncoder;
        this.key = key;
        this.issuer = issuer;
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(key.publicKey())
                .signatureAlgorithm(AuthConfiguration.SIGNATURE_ALGORITHM).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuer),
                jwt -> TokenClaimNames.ACCESS.equals(jwt.getClaimAsString(TokenClaimNames.TOKEN_USE))
                        ? OAuth2TokenValidatorResult.failure(new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN,
                                "An access token cannot be used as a refresh token", null))
                        : OAuth2TokenValidatorResult.success()));
        this.refreshTokenDecoder = decoder;
    }

    @Override
    public String issue(TokenClaims claims, Duration lifetime) {
        return sign(base(claims.subject(), lifetime)
                .claim(TokenClaimNames.TOKEN_USE, TokenClaimNames.ACCESS)
                .claim(TokenClaimNames.USER, claims.user())
                .claim(TokenClaimNames.PERMISSIONS, claims.permissions())
                .build());
    }

    @Override
    public String issueRefresh(String subject, Duration lifetime) {
        return sign(base(subject, lifetime).claim(TokenClaimNames.TOKEN_USE, TokenClaimNames.REFRESH).build());
    }

    private JwtClaimsSet.Builder base(String subject, Duration lifetime) {
        Instant now = Instant.now();
        return JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())     // two tokens issued in the same second must still differ
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(lifetime))
                .subject(subject);
    }

    private String sign(JwtClaimsSet claims) {
        JwsHeader header = JwsHeader.with(AuthConfiguration.SIGNATURE_ALGORITHM).keyId(key.keyId()).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    /** The subject of a refresh token; an access token is refused here as a refresh token is refused by the APIs. */
    @Override
    public String subjectOf(String refreshToken) {
        try {
            return refreshTokenDecoder.decode(refreshToken).getSubject();
        } catch (JwtException e) {
            throw new InvalidTokenException(e instanceof BadJwtException ? e.getMessage() : null);
        }
    }
}
