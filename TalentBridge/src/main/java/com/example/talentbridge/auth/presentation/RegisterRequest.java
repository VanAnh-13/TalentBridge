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

    @NotBlank(message = "Tên người dùng không được để trống")
    private String name;

    @NotBlank(message = "Email người dùng không được để trống")
    @Email(
            message = "Định dạng email không hợp lệ",
            regexp = "^[\\w\\-.]+@([\\w\\-]+\\.)+[\\w\\-]{2,4}$"
    )
    private String email;

    @NotBlank(message = "Mật khẩu người dùng không được để trống")
    private String password;

    private LocalDate dob;
    private String address;
    private Gender gender;
    private boolean recruiter;
}
