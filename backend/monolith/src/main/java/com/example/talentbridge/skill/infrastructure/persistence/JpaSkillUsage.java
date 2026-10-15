package com.example.talentbridge.skill.infrastructure.persistence;

import com.example.talentbridge.skill.application.port.SkillUsage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class JpaSkillUsage implements SkillUsage {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long jobsRequiring(Long skillId) {
        return ((Number) entityManager
                .createNativeQuery("select count(*) from job_skill where skill_id = :id")
                .setParameter("id", skillId)
                .getSingleResult()).longValue();
    }

    @Override
    public void detachFromSubscribers(Long skillId) {
        entityManager.createNativeQuery("delete from subscriber_skills where skill_id = :id")
                .setParameter("id", skillId)
                .executeUpdate();
        entityManager.clear();   // loaded subscribers would still list the skill
    }
}
