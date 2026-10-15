package com.example.talentbridge.access.infrastructure.persistence;

import com.example.talentbridge.model.User;
import com.example.talentbridge.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class RoleJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToMany
    @JoinTable(
            name = "roles_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @ToString.Exclude
    private Set<PermissionJpaEntity> permissions;

    @OneToMany(mappedBy = "role")
    @ToString.Exclude
    private List<User> users;
}
