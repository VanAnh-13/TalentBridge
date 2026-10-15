package com.example.talentbridge.shared.security.autoconfigure;

import com.example.talentbridge.shared.security.TokenClaimNames;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * For services that do not issue tokens: verify RS256 access tokens with the public keys the auth module publishes
 * at {@code /.well-known/jwks.json} ({@code jwt.jwk-set-uri}), and insist on our issuer. Keys are fetched on first use
 * and refetched when a token names an unknown key id, so services do not depend on start-up order.
 * The token issuer (the monolith) declares its own decoder and this one steps aside.
 */
@AutoConfiguration(before = OAuth2ResourceServerAutoConfiguration.class)
@ConditionalOnProperty(prefix = "jwt", name = "jwk-set-uri")
public class JwksResourceServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    public JwtDecoder jwksJwtDecoder(@Value("${jwt.jwk-set-uri}") String jwkSetUri,
                                     @Value("${jwt.issuer:" + TokenClaimNames.DEFAULT_ISSUER + "}") String issuer) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .jwsAlgorithm(SignatureAlgorithm.RS256)
                .build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuer)));
        return decoder;
    }
}
