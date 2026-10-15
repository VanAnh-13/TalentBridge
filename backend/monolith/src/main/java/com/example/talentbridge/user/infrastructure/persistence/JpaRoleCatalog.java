package com.example.talentbridge.user.infrastructure.persistence;

import com.example.talentbridge.access.infrastructure.persistence.RoleJpaEntity;
import com.example.talentbridge.user.application.port.RoleCatalog;
import com.example.talentbridge.user.application.port.RoleNames;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaRoleCatalog implements RoleCatalog, RoleNames {

    private final EntityManager entityManager;

    @Override
    public boolean exists(Long roleId) {
        return entityManager.find(RoleJpaEntity.class, roleId) != null;
    }

    @Override
    public Optional<Long> idOfName(String roleName) {
        return entityManager
                .createQuery("select r.id from RoleJpaEntity r where r.name = :name", Long.class)
                .setParameter("name", roleName)
                .getResultStream()
                .findFirst();
    }

    @Override
    public String nameOf(Long roleId) {
        RoleJpaEntity role = entityManager.find(RoleJpaEntity.class, roleId);
        return role == null ? null : role.getName();
    }
}
