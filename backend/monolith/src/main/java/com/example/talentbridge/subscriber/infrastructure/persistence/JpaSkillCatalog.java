package com.example.talentbridge.subscriber.infrastructure.persistence;

import com.example.talentbridge.skill.infrastructure.persistence.SkillJpaEntity;
import com.example.talentbridge.subscriber.application.port.SkillCatalog;
import com.example.talentbridge.subscriber.domain.SubscribedSkill;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaSkillCatalog implements SkillCatalog {

    private final EntityManager entityManager;

    @Override
    public List<SubscribedSkill> findExisting(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return List.of();

        return entityManager
                .createQuery("select s from SkillJpaEntity s where s.id in :ids", SkillJpaEntity.class)
                .setParameter("ids", ids)
                .getResultList()
                .stream()
                .map(s -> new SubscribedSkill(s.getId(), s.getName()))
                .toList();
    }
}
