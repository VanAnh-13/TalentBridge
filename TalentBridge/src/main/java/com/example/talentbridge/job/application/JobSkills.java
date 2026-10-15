package com.example.talentbridge.job.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.job.application.port.SkillCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/** Shared rules for resolving the skills of a job. */
@Component
@RequiredArgsConstructor
class JobSkills {

    private final SkillCatalog skillCatalog;

    /** Every requested skill must exist (creation). */
    List<Long> requireAllExisting(List<Long> ids) {
        Set<Long> existing = skillCatalog.existingIds(ids);
        if (existing.size() != ids.size())
            throw new NotFoundException("Kỹ năng không tồn tại");
        return ids;
    }

    /** Unknown ids are ignored (update). */
    List<Long> onlyExisting(List<Long> ids) {
        Set<Long> existing = skillCatalog.existingIds(ids);
        return ids.stream().filter(existing::contains).distinct().toList();
    }
}
