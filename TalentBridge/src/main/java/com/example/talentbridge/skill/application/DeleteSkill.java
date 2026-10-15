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
                .orElseThrow(() -> new NotFoundException("Không tìm thấy kỹ năng"));

        long jobs = usage.jobsRequiring(id);
        if (jobs > 0)
            throw new BusinessRuleViolationException("Không thể xóa kỹ năng đang được " + jobs + " công việc yêu cầu");

        usage.detachFromSubscribers(id);
        skills.delete(skill);
        return skill;
    }
}
