package com.example.talentbridge.skill.infrastructure.persistence;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import com.example.talentbridge.shared.infrastructure.persistence.FilterSpecifications;
import com.example.talentbridge.shared.infrastructure.persistence.PagingMapper;
import com.example.talentbridge.skill.domain.Skill;
import com.example.talentbridge.skill.domain.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** JPA implementation of the skill module's repository port. */
@Component
@RequiredArgsConstructor
public class SkillRepositoryAdapter implements SkillRepository {

    private final SkillJpaRepository jpa;
    private final FilterSpecifications filters;

    @Override
    public Skill save(Skill skill) {
        // Load the managed entity on update so relations the domain does not model (jobs, subscribers) are kept.
        SkillJpaEntity entity = skill.getId() == null
                ? new SkillJpaEntity()
                : jpa.findById(skill.getId()).orElseThrow(() -> new NotFoundException("Không tìm thấy kỹ năng"));

        entity.setName(skill.getName());
        return toDomain(jpa.saveAndFlush(entity));
    }

    @Override
    public Optional<Skill> findById(Long id) {
        return jpa.findById(id).map(SkillRepositoryAdapter::toDomain);
    }

    @Override
    public PageResult<Skill> search(FilterExpression filter, PageQuery page) {
        return PagingMapper.toPageResult(
                jpa.findAll(filters.<SkillJpaEntity>toSpecification(filter), PagingMapper.toPageable(page)),
                SkillRepositoryAdapter::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return jpa.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        return jpa.existsByNameAndIdNot(name, id);
    }

    @Override
    public void delete(Skill skill) {
        jpa.deleteById(skill.getId());
    }

    private static Skill toDomain(SkillJpaEntity e) {
        return Skill.restore(e.getId(), e.getName(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
