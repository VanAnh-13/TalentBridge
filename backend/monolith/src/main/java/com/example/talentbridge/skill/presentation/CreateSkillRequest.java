package com.example.talentbridge.skill.presentation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateSkillRequest {

    @NotBlank(message = "Tên kỹ năng không được để trống")
    private String name;
}
