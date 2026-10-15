package com.example.talentbridge.auth.infrastructure.security;

import com.example.talentbridge.auth.application.TokenClaims;
import com.example.talentbridge.auth.application.port.TokenIssuer;
import com.example.talentbridge.auth.application.port.TokenReader;
import com.example.talentbridge.shared.domain.exception.InvalidTokenException;
import com.example.talentbridge.shared.security.TokenClaimNames;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/** Signs (RS256) and reads the JWTs used as access and refresh tokens. */
@Component
public class JwtTokenService implements TokenIssuer, TokenReader {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final RsaSigningKey key;
    private final String issuer;

    public JwtTokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, RsaSigningKey key,
                           @Value("${jwt.issuer:" + TokenClaimNames.DEFAULT_ISSUER + "}") String issuer) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.key = key;
        this.issuer = issuer;
    }

    @Override
    public String issue(TokenClaims claims, Duration lifetime) {
        Instant now = Instant.now();

        JwtClaimsSet set = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())     // two tokens issued in the same second must still differ
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(lifetime))
                .subject(claims.subject())
                .claim(TokenClaimNames.USER, claims.user())
                .claim(TokenClaimNames.PERMISSIONS, claims.permissions())
                .build();

        JwsHeader header = JwsHeader.with(AuthConfiguration.SIGNATURE_ALGORITHM).keyId(key.keyId()).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, set)).getTokenValue();
    }

    @Override
    public String subjectOf(String token) {
        try {
            return jwtDecoder.decode(token).getSubject();
        } catch (JwtException e) {
            throw new InvalidTokenException(e instanceof BadJwtException ? e.getMessage() : null);
        }
    }
}
