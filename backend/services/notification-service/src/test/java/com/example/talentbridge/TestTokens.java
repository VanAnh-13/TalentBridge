package com.example.talentbridge;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Stands in for the auth service: signs access tokens shaped like the real ones (RS256, iss, user claim,
 * permissions) and makes this service verify them with the matching public key.
 */
@TestConfiguration
public class TestTokens {

    public static final List<String> INBOX = List.of("GET /notifications", "GET /notifications/unread-count",
            "PATCH /notifications/{id}/read", "POST /notifications/read-all");

    private static final RSAKey KEY = generate();

    private static RSAKey generate() {
        try {
            return new RSAKeyGenerator(2048).keyID("test").generate();
        } catch (JOSEException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static String token(long userId, List<String> permissions) {
        return sign(userId, permissions, "talentbridge-auth", KEY);
    }

    /** A token signed by a key this service does not trust. */
    public static String foreignToken(long userId) {
        return sign(userId, INBOX, "talentbridge-auth", generate());
    }

    private static String sign(long userId, List<String> permissions, String issuer, RSAKey key) {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject("user" + userId + "@test.com").issuer(issuer)
                    .claim("user", Map.of("id", userId, "email", "user" + userId + "@test.com"))
                    .claim("permissions", permissions)
                    .issueTime(new Date()).expirationTime(new Date(System.currentTimeMillis() + 600_000)).build();
            SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key.getKeyID()).build(), claims);
            jwt.sign(new RSASSASigner(key));
            return jwt.serialize();
        } catch (JOSEException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Bean
    @Primary
    JwtDecoder testJwtDecoder() throws JOSEException {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(KEY.toRSAPublicKey()).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer("talentbridge-auth")));
        return decoder;
    }
}
