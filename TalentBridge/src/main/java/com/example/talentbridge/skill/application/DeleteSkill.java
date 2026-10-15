package com.example.talentbridge.skill.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.skill.domain.Skill;
import com.example.talentbridge.skill.domain.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteSkill {

    private final SkillRepository skills;

    public Skill execute(Long id) {
        Skill skill = skills.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy kỹ năng"));

        skills.delete(skill);
        return skill;
    }
}
