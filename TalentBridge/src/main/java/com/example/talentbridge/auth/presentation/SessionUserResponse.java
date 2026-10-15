package com.example.talentbridge.auth.presentation;

import com.example.talentbridge.auth.application.SessionUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SessionUserResponse {

    private String email;
    private String name;
    private Long id;
    private String companyId;
    private String role;
    private List<String> permissions;
    private String logoUrl;
    private String updatedAt;

    public static SessionUserResponse from(SessionUser u) {
        return new SessionUserResponse(u.email(), u.name(), u.id(), u.companyId(), u.role(), u.permissions(), u.logoUrl(), u.updatedAt());
    }
}
