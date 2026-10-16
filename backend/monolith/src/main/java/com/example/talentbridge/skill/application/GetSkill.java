package com.example.talentbridge.skill.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.skill.domain.Skill;
import com.example.talentbridge.skill.domain.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSkill {

    private final SkillRepository skills;

    public Skill execute(Long id) {
        return skills.findById(id)
                .orElseThrow(() -> new NotFoundException("Skill not found"));
    }
}
