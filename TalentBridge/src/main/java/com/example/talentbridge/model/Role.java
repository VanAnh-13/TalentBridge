package com.example.talentbridge.model;

import jakarta.persistence.*;
import lombok.*;
import com.example.talentbridge.shared.infrastructure.persistence.BaseEntity;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Role extends BaseEntity {

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
    private Set<Permission> permissions;

    @OneToMany(mappedBy = "role")
    @ToString.Exclude
    private List<User> users;

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
        this.active = true;
    }

    public Role(String name, String description, boolean active) {
        this.name = name;
        this.description = description;
        this.active = active;
    }
}
