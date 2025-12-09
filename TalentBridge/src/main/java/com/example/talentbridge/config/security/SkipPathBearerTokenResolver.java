package com.example.talentbridge.config.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

import java.util.List;

public class SkipPathBearerTokenResolver implements BearerTokenResolver {

    private final BearerTokenResolver delegate = new DefaultBearerTokenResolver();

    // Paths that require authentication (should NOT skip token)
    private final List<String> authenticatedPaths = List.of(
            "/companies/me",
            "/jobs/me"
    );

    // Paths that are public (skip token resolution)
    private final List<String> skipPaths = List.of(
            "/auth/login",
            "/auth/logout",
            "/auth/register",
            "/auth/refresh-token"
    );

    @Override
    public String resolve(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Always resolve token for authenticated paths
        for (String authPath : authenticatedPaths) {
            if (path.contains(authPath)) {
                return delegate.resolve(request);
            }
        }

        // Skip token for public paths
        for (String skip : skipPaths) {
            if (path.contains(skip)) {
                return null;
            }
        }
        
        return delegate.resolve(request);
    }
}
