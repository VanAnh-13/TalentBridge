package com.example.talentbridge.auth.infrastructure.security;

import com.example.talentbridge.shared.security.TokenClaimNames;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;

/** This application issues the tokens: it signs with the private key and verifies with the public one locally. */
@Configuration
public class AuthConfiguration {

    public static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.RS256;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtEncoder jwtEncoder(RsaSigningKey key) {
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key.signingKey())));
    }

    @Bean
    public JwtDecoder jwtDecoder(RsaSigningKey key,
                                 @Value("${jwt.issuer:" + TokenClaimNames.DEFAULT_ISSUER + "}") String issuer) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(key.publicKey())
                .signatureAlgorithm(SIGNATURE_ALGORITHM)
                .build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuer)));
        return decoder;
    }
}
