package com.example.talentbridge.auth.application;

import com.example.talentbridge.user.domain.UserAccount;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The signed-in user as the client sees it (also embedded in the access token). */
public record SessionUser(String email, String name, Long id, String companyId, String role,
                          List<String> permissions, String logoUrl, String updatedAt) {

    public static SessionUser from(UserAccount account) {
        var user = account.user();
        return new SessionUser(
                user.getEmail(), user.getName(), user.getId(),
                user.getCompanyId() == null ? null : user.getCompanyId().toString(),
                account.role() == null ? null : account.role().name(),
                account.permissions(), user.getLogoUrl(),
                user.getUpdatedAt() == null ? null : user.getUpdatedAt().toString());
    }

    /** Field order and names are part of the token format the frontend reads. */
    public Map<String, Object> asClaim() {
        Map<String, Object> claim = new LinkedHashMap<>();
        claim.put("email", email);
        claim.put("name", name);
        claim.put("id", id);
        claim.put("companyId", companyId);
        claim.put("role", role);
        claim.put("permissions", permissions);
        claim.put("logoUrl", logoUrl);
        claim.put("updatedAt", updatedAt);
        return claim;
    }
}
