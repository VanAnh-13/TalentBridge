package com.example.talentbridge.subscriber.infrastructure.persistence;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.skill.infrastructure.persistence.SkillJpaEntity;
import com.example.talentbridge.subscriber.domain.SubscribedSkill;
import com.example.talentbridge.subscriber.domain.Subscriber;
import com.example.talentbridge.subscriber.domain.SubscriberRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubscriberRepositoryAdapter implements SubscriberRepository {

    private final SubscriberJpaRepository jpa;
    private final EntityManager entityManager;

    @Override
    public Subscriber save(Subscriber subscriber) {
        SubscriberJpaEntity entity = subscriber.getId() == null
                ? new SubscriberJpaEntity(subscriber.getEmail())
                : jpa.findById(subscriber.getId()).orElseThrow(() -> new NotFoundException("Không tìm thấy đăng ký người dùng này"));

        List<SkillJpaEntity> skills = new ArrayList<>();
        for (SubscribedSkill skill : subscriber.getSkills())
            skills.add(entityManager.getReference(SkillJpaEntity.class, skill.id()));
        entity.setSkills(skills);

        return toDomain(jpa.saveAndFlush(entity));
    }

    @Override
    public Optional<Subscriber> findByEmail(String email) {
        return jpa.findByEmail(email).map(SubscriberRepositoryAdapter::toDomain);
    }

    @Override
    public void delete(Subscriber subscriber) {
        SubscriberJpaEntity entity = jpa.findById(subscriber.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đăng ký người dùng này"));
        entity.setSkills(null);
        jpa.delete(entity);
    }

    static Subscriber toDomain(SubscriberJpaEntity e) {
        List<SubscribedSkill> skills = e.getSkills() == null
                ? List.of()
                : e.getSkills().stream().map(s -> new SubscribedSkill(s.getId(), s.getName())).toList();
        return Subscriber.restore(e.getId(), e.getEmail(), skills);
    }
}
