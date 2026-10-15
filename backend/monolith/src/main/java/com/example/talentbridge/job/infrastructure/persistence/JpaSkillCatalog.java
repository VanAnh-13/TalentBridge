package com.example.talentbridge.job.infrastructure.persistence;

import com.example.talentbridge.job.application.port.SkillCatalog;
import com.example.talentbridge.skill.infrastructure.persistence.SkillJpaEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component("jobSkillCatalog")
@RequiredArgsConstructor
public class JpaSkillCatalog implements SkillCatalog {

    private final EntityManager entityManager;

    @Override
    public Set<Long> existingIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty())
            return Set.of();
        return new HashSet<>(entityManager
                .createQuery("select s.id from SkillJpaEntity s where s.id in :ids", Long.class)
                .setParameter("ids", ids)
                .getResultList());
    }
}
