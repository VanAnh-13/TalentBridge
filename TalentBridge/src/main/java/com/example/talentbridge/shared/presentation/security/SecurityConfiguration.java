package com.example.talentbridge.shared.presentation.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final String[] WHITELIST = {
            // LOGIN
            "/auth/login",
            "/auth/logout",
            "/auth/register",
            "/auth/refresh-token",

            // PASSWORD RESET (used by people who cannot log in)
            "/auth/forgot-password",
            "/auth/reset-password",
            "/auth/reset-password/verify",

            // API DOCS
            "/swagger-ui/**",
            "/v3/api-docs/**",

            // ACTUATOR
            "/actuator/**"
    };

    /** Read-only public browsing; every write on these resources requires authentication. */
    private static final String[] PUBLIC_GET = {
            "/companies/**",
            "/jobs/**"
    };

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity httpSecurity,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint
    ) throws Exception {
        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET).permitAll()
                        .anyRequest().authenticated()
                )
                .cors(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .bearerTokenResolver(new SkipPathBearerTokenResolver())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);


        return httpSecurity.build();
    }


}
