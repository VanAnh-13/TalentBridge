package com.example.talentbridge.model;

import com.example.talentbridge.access.infrastructure.persistence.RoleJpaEntity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;
import com.example.talentbridge.shared.infrastructure.persistence.BaseEntity;
import com.example.talentbridge.model.constant.Gender;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@JsonPropertyOrder({"id", "name", "email", "password", "age", "address", "gender"})
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private LocalDate dob;

    private String address;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String logoUrl;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @ToString.Exclude
    private Company company;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Resume> resumes;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @ToString.Exclude
    private RoleJpaEntity role;

    public User(String email, String name, String password, LocalDate dob, String address, Gender gender) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.dob = dob;
        this.address = address;
        this.gender = gender;
    }
}

