package com.example.talentbridge.shared.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * The token's {@code permissions} claim becomes the caller's authorities, unprefixed, so endpoints are guarded with
 * {@code @PreAuthorize("hasAuthority('GET /path')")} in every service alike.
 */
@Configuration
public class PermissionAuthoritiesConfiguration {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthorityPrefix("");
        authorities.setAuthoritiesClaimName(TokenClaimNames.PERMISSIONS);

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }
}
