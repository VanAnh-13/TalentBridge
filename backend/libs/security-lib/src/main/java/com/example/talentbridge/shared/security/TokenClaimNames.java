package com.example.talentbridge.shared.security;

/** The claims every TalentBridge access token carries; the auth module writes them, every service reads them. */
public final class TokenClaimNames {

    /** Value of the {@code iss} claim unless {@code jwt.issuer} says otherwise. */
    public static final String DEFAULT_ISSUER = "talentbridge-auth";

    /** Permission names ("METHOD /path"), used as Spring authorities as they are. */
    public static final String PERMISSIONS = "permissions";

    /** Snapshot of the user: id, email, name, companyId, role, ... */
    public static final String USER = "user";

    private TokenClaimNames() {
    }
}
