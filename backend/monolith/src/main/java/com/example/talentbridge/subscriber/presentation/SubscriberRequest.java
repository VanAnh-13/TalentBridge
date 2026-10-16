package com.example.talentbridge.subscriber.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberRequest {

    @NotNull(message = "Skills are required")
    @Size(min = 1, message = "At least one skill is required")
    @Valid
    private List<SkillId> skills;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SkillId {
        @NotNull(message = "Skill id is required")
        private Long id;
    }

    public List<Long> skillIds() {
        return skills.stream().map(SkillId::getId).toList();
    }
}
