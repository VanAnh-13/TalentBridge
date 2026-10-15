package com.example.talentbridge.subscriber.presentation;

import com.example.talentbridge.subscriber.domain.Subscriber;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberResponse {

    private Long id;
    private String email;
    private List<SkillDto> skills;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SkillDto {
        private Long id;
        private String name;
    }

    public static SubscriberResponse from(Subscriber subscriber) {
        return new SubscriberResponse(
                subscriber.getId(),
                subscriber.getEmail(),
                subscriber.getSkills().stream().map(s -> new SkillDto(s.id(), s.name())).toList()
        );
    }
}
