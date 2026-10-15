package com.example.talentbridge.shared.security;

import com.example.talentbridge.shared.security.autoconfigure.JwksResourceServerAutoConfiguration;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/** A service that only holds the JWKS address accepts our tokens and nothing else. */
class JwksResourceServerAutoConfigurationTest {

    static HttpServer server;
    static RSAKey key;
    static RSAKey stranger;
    static final AtomicInteger fetches = new AtomicInteger();

    @BeforeAll
    static void serveKeys() throws Exception {
        key = new RSAKeyGenerator(2048).keyID("k1").generate();
        stranger = new RSAKeyGenerator(2048).keyID("k1").generate();
        byte[] body = new JWKSet(key.toPublicJWK()).toString().getBytes(StandardCharsets.UTF_8);
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/.well-known/jwks.json", exchange -> {
            fetches.incrementAndGet();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
    }

    @AfterAll
    static void stop() {
        server.stop(0);
    }

    private static String jwksUri() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/.well-known/jwks.json";
    }

    private static String token(RSAKey signer, String issuer, long ttlMillis) throws Exception {
        return token(signer, issuer, ttlMillis, "access");
    }

    private static String token(RSAKey signer, String issuer, long ttlMillis, String use) throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("token_use", use)
                .subject("a@b.com").issuer(issuer)
                .claim("user", Map.of("id", 42, "email", "a@b.com"))
                .claim("permissions", List.of("GET /notifications"))
                .issueTime(new Date()).expirationTime(new Date(System.currentTimeMillis() + ttlMillis)).build();
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(signer.getKeyID()).build(), claims);
        jwt.sign(new RSASSASigner(signer));
        return jwt.serialize();
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JwksResourceServerAutoConfiguration.class));

    @Test @DisplayName("with jwt.jwk-set-uri a decoder verifies RS256 tokens of our issuer and keys only")
    void verifies() {
        runner.withPropertyValues("jwt.jwk-set-uri=" + jwksUri()).run(ctx -> {
            JwtDecoder decoder = ctx.getBean(JwtDecoder.class);
            Jwt jwt = decoder.decode(token(key, TokenClaimNames.DEFAULT_ISSUER, 60_000));
            assertEquals(42L, SecurityContextCurrentUser.userIdOf(jwt));
            assertThrows(JwtException.class, () -> decoder.decode(token(stranger, TokenClaimNames.DEFAULT_ISSUER, 60_000)));
            assertThrows(JwtException.class, () -> decoder.decode(token(key, "someone-else", 60_000)));
            assertThrows(JwtException.class, () -> decoder.decode(token(key, TokenClaimNames.DEFAULT_ISSUER, -120_000)));
            assertThrows(JwtException.class, () -> decoder.decode(token(key, TokenClaimNames.DEFAULT_ISSUER, 60_000, "refresh")),
                    "a refresh token is not a bearer token");
            assertThat(fetches.get()).isPositive();
        });
    }

    @Test @DisplayName("a service that issues tokens keeps its own decoder; without the property nothing is added")
    void stepsAside() {
        JwtDecoder own = token -> { throw new JwtException("own"); };
        runner.withPropertyValues("jwt.jwk-set-uri=" + jwksUri())
                .withBean(JwtDecoder.class, () -> own)
                .run(ctx -> assertSame(own, ctx.getBean(JwtDecoder.class)));
        runner.run(ctx -> assertThat(ctx).doesNotHaveBean(JwtDecoder.class));
    }
}
