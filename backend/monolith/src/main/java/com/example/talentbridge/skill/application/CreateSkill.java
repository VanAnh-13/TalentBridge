package com.example.talentbridge.skill.application;

import com.example.talentbridge.shared.domain.exception.AlreadyExistsException;
import com.example.talentbridge.skill.domain.Skill;
import com.example.talentbridge.skill.domain.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateSkill {

    private final SkillRepository skills;

    public Skill execute(String name) {
        if (skills.existsByName(name))
            throw new AlreadyExistsException("This skill already exists");

        return skills.save(Skill.create(name));
    }
}
