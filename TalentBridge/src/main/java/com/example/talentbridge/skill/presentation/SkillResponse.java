package com.example.talentbridge.skill.presentation;

import com.example.talentbridge.skill.domain.Skill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SkillResponse {

    private Long id;
    private String name;
    private String createdAt;
    private String updatedAt;

    public static SkillResponse from(Skill skill) {
        return new SkillResponse(
                skill.getId(),
                skill.getName(),
                skill.getCreatedAt() == null ? null : skill.getCreatedAt().toString(),
                skill.getUpdatedAt() == null ? null : skill.getUpdatedAt().toString()
        );
    }
}
