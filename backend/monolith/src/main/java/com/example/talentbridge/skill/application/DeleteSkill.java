package com.example.talentbridge.skill.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.domain.exception.BusinessRuleViolationException;
import com.example.talentbridge.skill.application.port.SkillUsage;
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
    private final SkillUsage usage;

    public Skill execute(Long id) {
        Skill skill = skills.findById(id)
                .orElseThrow(() -> new NotFoundException("Skill not found"));

        long jobs = usage.jobsRequiring(id);
        if (jobs > 0)
            throw new BusinessRuleViolationException("This skill cannot be deleted: it is required by " + jobs + " job(s)");

        usage.detachFromSubscribers(id);
        skills.delete(skill);
        return skill;
    }
}
