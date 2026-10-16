package com.example.talentbridge.auth.presentation;

import com.example.talentbridge.user.domain.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest {

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
    private boolean recruiter;
}
