package com.example.talentbridge.skill.application;

import com.example.talentbridge.shared.domain.exception.AlreadyExistsException;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.skill.domain.Skill;
import com.example.talentbridge.skill.domain.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateSkill {

    private final SkillRepository skills;

    public Skill execute(Long id, String name) {
        Skill skill = skills.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy kỹ năng"));

        if (skills.existsByNameAndIdNot(name, id))
            throw new AlreadyExistsException("Kỹ năng này đã tồn tại");

        skill.rename(name);
        return skills.save(skill);
    }
}
