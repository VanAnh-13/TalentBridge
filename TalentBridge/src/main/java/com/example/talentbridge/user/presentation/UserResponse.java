package com.example.talentbridge.user.presentation;

import com.example.talentbridge.user.domain.Gender;
import com.example.talentbridge.user.domain.User;
import com.example.talentbridge.user.domain.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private LocalDate dob;
    private String address;
    private Gender gender;
    private String logoUrl;
    private CompanyInformation company;
    private RoleInformation role;
    private Instant createdAt;
    private Instant updatedAt;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class CompanyInformation {
        private Long id;
        private String name;
        private String address;
        private String logoUrl;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class RoleInformation {
        private Long id;
        private String name;
        private String description;
    }

    public static UserResponse from(UserDetails d) {
        User u = d.user();
        return new UserResponse(
                u.getId(), u.getName(), u.getEmail(), u.getDob(), u.getAddress(), u.getGender(), u.getLogoUrl(),
                d.company() == null ? null : new CompanyInformation(
                        d.company().id(), d.company().name(), d.company().address(), d.company().logoUrl()),
                d.role() == null ? null : new RoleInformation(d.role().id(), d.role().name(), d.role().description()),
                u.getCreatedAt(), u.getUpdatedAt());
    }
}
