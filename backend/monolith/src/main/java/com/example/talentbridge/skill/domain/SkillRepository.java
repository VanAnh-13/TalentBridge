package com.example.talentbridge.skill.domain;

import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;

import java.util.Optional;

/** Output port: how the skill module stores and queries skills. Implemented in infrastructure. */
public interface SkillRepository {

    Skill save(Skill skill);

    Optional<Skill> findById(Long id);

    PageResult<Skill> search(FilterExpression filter, PageQuery page);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    void delete(Skill skill);
}
