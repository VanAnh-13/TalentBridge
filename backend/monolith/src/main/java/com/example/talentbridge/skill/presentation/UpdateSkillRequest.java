package com.example.talentbridge.skill.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateSkillRequest {

    @NotNull(message = "Skill id is required")
    private Long id;

    @NotBlank(message = "Skill name is required")
    private String name;
}
