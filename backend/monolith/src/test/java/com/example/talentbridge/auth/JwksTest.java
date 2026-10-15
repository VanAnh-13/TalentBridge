package com.example.talentbridge.auth;

import com.example.talentbridge.controller.TestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Access tokens are RS256 and can be verified by anybody holding the published key set, and only with it. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwksTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String jwks() throws Exception {
        return mockMvc.perform(get("/.well-known/jwks.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keys[0].kty").value("RSA"))
                .andExpect(jsonPath("$.keys[0].d").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist())        // raw JWKS, not the ApiResponse envelope
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("max-age=300")))
                .andReturn().getResponse().getContentAsString();
    }

    @Test @DisplayName("the key set is public and a token from /auth/login verifies with it alone, issuer included")
    void verifiesWithPublishedKeys() throws Exception {
        RSAKey published = (RSAKey) JWKSet.parse(jwks()).getKeys().get(0);
        String token = TestHelper.getAdminToken(mockMvc, objectMapper);

        SignedJWT parsed = SignedJWT.parse(token);
        assertEquals(JWSAlgorithm.RS256, parsed.getHeader().getAlgorithm());
        assertEquals(published.getKeyID(), parsed.getHeader().getKeyID());

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(published.toRSAPublicKey()).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer("talentbridge-auth")));
        Jwt jwt = decoder.decode(token);
        assertEquals("test-admin@talentbridge.com", jwt.getSubject());
        assertTrue(((List<?>) jwt.getClaim("permissions")).contains("GET /admin"));
        assertNotNull(((Map<?, ?>) jwt.getClaim("user")).get("id"));
    }

    @Test @DisplayName("tokens stay small: permissions appear once; the refresh cookie carries no permissions and fits a browser cookie")
    void tokenSizes() throws Exception {
        var login = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/login")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test-admin@talentbridge.com\",\"password\":\"Admin@123456\","
                                + "\"sessionMetaRequest\":{\"deviceName\":\"t\",\"deviceType\":\"t\",\"userAgent\":\"t\"}}"))
                .andExpect(status().isOk()).andReturn().getResponse();
        String refresh = login.getCookie("refresh_token").getValue();
        String access = objectMapper.readTree(login.getContentAsString()).get("data").get("accessToken").asText();

        // the administrator holds every permission: the worst case
        assertTrue(refresh.length() < 1024, "refresh cookie is " + refresh.length() + " chars");
        assertTrue(("refresh_token=" + refresh).length() < 4096, "browsers drop cookies over 4 KB");
        var refreshClaims = SignedJWT.parse(refresh).getJWTClaimsSet();
        assertEquals("refresh", refreshClaims.getStringClaim("token_use"));
        assertNull(refreshClaims.getClaim("permissions"));
        assertNull(refreshClaims.getClaim("user"));

        var accessClaims = SignedJWT.parse(access).getJWTClaimsSet();
        assertEquals("access", accessClaims.getStringClaim("token_use"));
        assertFalse(((Map<?, ?>) accessClaims.getClaim("user")).containsKey("permissions"), "permissions are listed once");
        assertFalse(((List<?>) accessClaims.getClaim("permissions")).isEmpty());
    }

    @Test @DisplayName("a refresh token is not a bearer token, and an access token cannot refresh a session")
    void tokenUsesDoNotMix() throws Exception {
        var login = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/login")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test-admin@talentbridge.com\",\"password\":\"Admin@123456\","
                                + "\"sessionMetaRequest\":{\"deviceName\":\"t\",\"deviceType\":\"t\",\"userAgent\":\"t\"}}"))
                .andExpect(status().isOk()).andReturn().getResponse();
        String refresh = login.getCookie("refresh_token").getValue();
        String access = objectMapper.readTree(login.getContentAsString()).get("data").get("accessToken").asText();

        mockMvc.perform(get("/auth/me").header("Authorization", "Bearer " + refresh)).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/auth/me").header("Authorization", "Bearer " + access)).andExpect(status().isOk());

        String meta = "{\"deviceName\":\"t\",\"deviceType\":\"t\",\"userAgent\":\"t\"}";
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/refresh-token")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", access))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(meta))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/refresh-token")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", refresh))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(meta))
                .andExpect(status().isOk());
    }

    @Test @DisplayName("a token signed with a shared secret (the old HS512 scheme) or tampered with is refused")
    void refusesForeignTokens() throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder().subject("test-admin@talentbridge.com").issuer("talentbridge-auth")
                .claim("permissions", List.of("GET /users")).expirationTime(new Date(System.currentTimeMillis() + 60_000)).build();
        SignedJWT hs = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claims);
        hs.sign(new MACSigner(new byte[64]));
        mockMvc.perform(get("/users").header("Authorization", "Bearer " + hs.serialize()))
                .andExpect(status().isUnauthorized());

        String token = TestHelper.getAdminToken(mockMvc, objectMapper);
        String[] parts = token.split("\\.");
        String forged = parts[0] + "." + java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(
                "{\"sub\":\"someone@else.com\",\"permissions\":[\"GET /users\"],\"iss\":\"talentbridge-auth\"}".getBytes()) + "." + parts[2];
        mockMvc.perform(get("/users").header("Authorization", "Bearer " + forged))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
