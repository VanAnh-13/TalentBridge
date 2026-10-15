package com.example.talentbridge.access.infrastructure.persistence;

import com.example.talentbridge.access.domain.Permission;
import com.example.talentbridge.access.domain.Role;
import com.example.talentbridge.access.domain.RoleRepository;
import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import com.example.talentbridge.shared.infrastructure.persistence.FilterSpecifications;
import com.example.talentbridge.shared.infrastructure.persistence.PagingMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository jpa;
    private final EntityManager entityManager;
    private final FilterSpecifications filters;

    @Override
    public Role save(Role role) {
        RoleJpaEntity entity = role.getId() == null
                ? new RoleJpaEntity()
                : jpa.findById(role.getId()).orElseThrow(() -> new NotFoundException("Chức vụ không tồn tại"));

        entity.setName(role.getName());
        entity.setDescription(role.getDescription());
        entity.setActive(role.isActive());

        Set<PermissionJpaEntity> permissions = new HashSet<>();
        for (Permission p : role.getPermissions())
            permissions.add(entityManager.getReference(PermissionJpaEntity.class, p.getId()));
        entity.setPermissions(permissions);

        return toDomain(jpa.saveAndFlush(entity));
    }

    @Override
    public Optional<Role> findById(Long id) {
        return jpa.findById(id).map(RoleRepositoryAdapter::toDomain);
    }

    @Override
    public PageResult<Role> search(FilterExpression filter, PageQuery page) {
        return PagingMapper.toPageResult(
                jpa.findAll(filters.<RoleJpaEntity>toSpecification(filter), PagingMapper.toPageable(page)),
                RoleRepositoryAdapter::toDomain);
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
    public void delete(Role role) {
        RoleJpaEntity entity = jpa.findById(role.getId())
                .orElseThrow(() -> new NotFoundException("Chức vụ không tồn tại"));
        if (entity.getPermissions() != null)
            entity.getPermissions().clear();
        jpa.delete(entity);
    }

    static Role toDomain(RoleJpaEntity e) {
        List<Permission> permissions = e.getPermissions() == null
                ? List.of()
                : e.getPermissions().stream().map(PermissionRepositoryAdapter::toDomain).toList();
        return Role.restore(e.getId(), e.getName(), e.getDescription(), e.isActive(), permissions,
                e.getCreatedAt(), e.getUpdatedAt());
    }
}
