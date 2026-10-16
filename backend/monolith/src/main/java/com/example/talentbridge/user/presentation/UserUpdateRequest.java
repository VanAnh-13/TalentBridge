package com.example.talentbridge.user.presentation;

import com.example.talentbridge.user.application.UserData;
import com.example.talentbridge.user.domain.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateRequest {

    @NotNull(message = "Id is required")
    private Long id;

    private String name;
    private Gender gender;
    private LocalDate dob;
    private String address;
    private UserCreateRequest.CompanyId company;
    private UserCreateRequest.RoleId role;

    public UserData toData() {
        return new UserData(name, null, null, dob, address, gender,
                company == null ? null : company.getId(), role == null ? null : role.getId());
    }
}
