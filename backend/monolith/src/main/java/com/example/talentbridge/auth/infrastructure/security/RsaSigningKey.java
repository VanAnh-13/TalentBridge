package com.example.talentbridge.auth.infrastructure.security;

import com.example.talentbridge.auth.application.port.SigningKeys;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

/**
 * The RSA key pair that signs access and refresh tokens (RS256). Only the private key is configured
 * ({@code jwt.private-key}, PKCS#8 PEM, raw or base64-encoded); the public key is derived from it and published.
 * Without a key the application refuses to start, unless {@code jwt.generate-key-if-missing=true} (development and
 * tests only), which creates a throw-away key: every restart then signs everybody out.
 */
@Slf4j
@Component
public class RsaSigningKey implements SigningKeys {

    private static final int MIN_BITS = 2048;

    private final RSAKey key;

    public RsaSigningKey(@Value("${jwt.private-key:}") String privateKey,
                         @Value("${jwt.key-id:}") String keyId,
                         @Value("${jwt.generate-key-if-missing:false}") boolean generateIfMissing) {
        KeyPair pair;
        if (privateKey == null || privateKey.isBlank()) {
            if (!generateIfMissing)
                throw new IllegalStateException("jwt.private-key (env JWT_PRIVATE_KEY) is required: an RSA private key "
                        + "in PKCS#8 PEM, e.g. openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048");
            pair = generate();
            log.warn("No jwt.private-key configured: signing tokens with a throw-away RSA key (development only)");
        } else {
            pair = parse(privateKey);
        }
        RSAPublicKey publicKey = (RSAPublicKey) pair.getPublic();
        if (publicKey.getModulus().bitLength() < MIN_BITS)
            throw new IllegalStateException("jwt.private-key must be an RSA key of at least " + MIN_BITS + " bits");

        RSAKey.Builder builder = new RSAKey.Builder(publicKey)
                .privateKey(pair.getPrivate())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256);
        try {
            this.key = keyId == null || keyId.isBlank() ? builder.keyIDFromThumbprint().build() : builder.keyID(keyId).build();
        } catch (JOSEException ex) {
            throw new IllegalStateException("Could not compute the key id", ex);
        }
    }

    /** The key pair, private half included; never leaves this process. */
    RSAKey signingKey() {
        return key;
    }

    RSAPublicKey publicKey() {
        try {
            return key.toRSAPublicKey();
        } catch (JOSEException ex) {
            throw new IllegalStateException(ex);
        }
    }

    String keyId() {
        return key.getKeyID();
    }

    @Override
    public Map<String, Object> publicJwkSet() {
        return new JWKSet(key.toPublicJWK()).toJSONObject(true);
    }

    static KeyPair parse(String configured) {
        String text = configured.strip().replace("\\n", "\n");
        if (!text.contains("-----BEGIN")) {
            // an env-friendly base64 of the whole PEM file
            String decoded = new String(Base64.getMimeDecoder().decode(text), StandardCharsets.US_ASCII);
            if (!decoded.contains("-----BEGIN"))
                throw new IllegalStateException("jwt.private-key is neither a PEM key nor a base64-encoded PEM key");
            text = decoded;
        }
        if (text.contains("BEGIN RSA PRIVATE KEY"))
            throw new IllegalStateException("jwt.private-key is in PKCS#1 format; convert it with "
                    + "openssl pkcs8 -topk8 -nocrypt -in key.pem -out key-pkcs8.pem");
        if (!text.contains("BEGIN PRIVATE KEY"))
            throw new IllegalStateException("jwt.private-key must be an unencrypted PKCS#8 private key (BEGIN PRIVATE KEY)");
        String body = text.replaceAll("-----(BEGIN|END) PRIVATE KEY-----", "").replaceAll("\\s", "");
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) factory.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(body)));
            RSAPublicKey publicKey = (RSAPublicKey) factory.generatePublic(
                    new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent()));
            return new KeyPair(publicKey, privateKey);
        } catch (Exception ex) {
            throw new IllegalStateException("jwt.private-key could not be read as an RSA private key", ex);
        }
    }

    private static KeyPair generate() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(MIN_BITS);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
