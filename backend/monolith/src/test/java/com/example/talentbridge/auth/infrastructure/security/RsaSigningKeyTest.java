package com.example.talentbridge.auth.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RsaSigningKeyTest {

    private static KeyPair rsa(int bits) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(bits);
        return generator.generateKeyPair();
    }

    private static String pem(KeyPair pair) {
        return "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(pair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----\n";
    }

    @Test @DisplayName("production refuses to start without a key; development may use a throw-away one")
    void missingKey() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> new RsaSigningKey("", "", false));
        assertTrue(ex.getMessage().contains("JWT_PRIVATE_KEY"));
        assertNotNull(new RsaSigningKey(" ", "", true).keyId());
    }

    @Test @DisplayName("the same key reads alike as raw PEM, base64 of the PEM, or PEM with escaped newlines")
    void formats() throws Exception {
        String pem = pem(rsa(2048));
        String kid = new RsaSigningKey(pem, "", false).keyId();
        assertEquals(kid, new RsaSigningKey(Base64.getEncoder().encodeToString(pem.getBytes(StandardCharsets.US_ASCII)), "", false).keyId());
        assertEquals(kid, new RsaSigningKey(pem.replace("\n", "\\n"), "", false).keyId());
        assertEquals("k-2026", new RsaSigningKey(pem, "k-2026", false).keyId());
    }

    @Test @DisplayName("PKCS#1, garbage and short keys are refused with a useful message")
    void refusals() throws Exception {
        String pkcs1 = "-----BEGIN RSA PRIVATE KEY-----\nAAAA\n-----END RSA PRIVATE KEY-----";
        assertTrue(assertThrows(IllegalStateException.class, () -> new RsaSigningKey(pkcs1, "", false))
                .getMessage().contains("pkcs8"));
        assertThrows(IllegalStateException.class, () -> new RsaSigningKey("not a key", "", false));
        assertTrue(assertThrows(IllegalStateException.class, () -> new RsaSigningKey(pem(rsa(1024)), "", false))
                .getMessage().contains("2048"));
    }

    @Test @DisplayName("the published key set holds the public key only")
    @SuppressWarnings("unchecked")
    void publicOnly() {
        RsaSigningKey key = new RsaSigningKey("", "", true);
        Map<String, Object> jwk = ((java.util.List<Map<String, Object>>) key.publicJwkSet().get("keys")).get(0);
        assertEquals("RSA", jwk.get("kty"));
        assertEquals("RS256", jwk.get("alg"));
        assertEquals("sig", jwk.get("use"));
        assertEquals(key.keyId(), jwk.get("kid"));
        assertTrue(jwk.containsKey("n") && jwk.containsKey("e"));
        for (String secret : new String[]{"d", "p", "q", "dp", "dq", "qi"})
            assertFalse(jwk.containsKey(secret), "private parameter " + secret + " must not be published");
    }
}
