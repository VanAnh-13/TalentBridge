package com.example.talentbridge.company.presentation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberRequest {

    @NotBlank(message = "Email không được để trống")
    private String email;
}
