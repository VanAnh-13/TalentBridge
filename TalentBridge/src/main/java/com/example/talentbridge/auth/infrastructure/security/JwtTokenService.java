package com.example.talentbridge.auth.infrastructure.security;

import com.example.talentbridge.auth.application.TokenClaims;
import com.example.talentbridge.auth.application.port.TokenIssuer;
import com.example.talentbridge.auth.application.port.TokenReader;
import com.example.talentbridge.shared.domain.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/** Signs and reads the HS512 JWTs used as access and refresh tokens. */
@Component
@RequiredArgsConstructor
public class JwtTokenService implements TokenIssuer, TokenReader {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Override
    public String issue(TokenClaims claims, Duration lifetime) {
        Instant now = Instant.now();

        JwtClaimsSet set = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())     // two tokens issued in the same second must still differ
                .issuedAt(now)
                .expiresAt(now.plus(lifetime))
                .subject(claims.subject())
                .claim("user", claims.user())
                .claim("permissions", claims.permissions())
                .build();

        JwsHeader header = JwsHeader.with(AuthConfiguration.MAC_ALGORITHM).build();
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
