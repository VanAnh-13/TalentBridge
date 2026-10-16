package com.example.talentbridge.user.presentation;

import com.example.talentbridge.user.application.UserData;
import com.example.talentbridge.user.domain.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreateRequest {

    @NotBlank(message = "User name is required")
    private String name;

    @NotBlank(message = "User email is required")
    @Email(
            message = "Invalid email format",
            regexp = "^[\\w\\-.]+@([\\w\\-]+\\.)+[\\w\\-]{2,4}$"
    )
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private LocalDate dob;
    private String address;
    private Gender gender;
    private CompanyId company;
    private RoleId role;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class CompanyId {
        @NotNull(message = "Company is required")
        private Long id;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class RoleId {
        @NotNull(message = "Role is required")
        private Long id;
    }

    public UserData toData() {
        return new UserData(name, email, password, dob, address, gender,
                company == null ? null : company.getId(), role == null ? null : role.getId());
    }
}
