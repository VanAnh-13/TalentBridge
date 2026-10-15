package com.example.talentbridge.auth.presentation;

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
public class AccountDetailsResponse {

    private Long id;
    private String name;
    private String email;
    private LocalDate dob;
    private String address;
    private Gender gender;
    private String logoUrl;
    private Instant createdAt;
    private Instant updatedAt;

    public static AccountDetailsResponse from(UserDetails d) {
        User u = d.user();
        return new AccountDetailsResponse(u.getId(), u.getName(), u.getEmail(), u.getDob(), u.getAddress(),
                u.getGender(), u.getLogoUrl(), u.getCreatedAt(), u.getUpdatedAt());
    }
}
