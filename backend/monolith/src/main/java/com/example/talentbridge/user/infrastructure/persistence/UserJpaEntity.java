package com.example.talentbridge.user.infrastructure.persistence;

import com.example.talentbridge.company.infrastructure.persistence.CompanyJpaEntity;
import com.example.talentbridge.access.infrastructure.persistence.RoleJpaEntity;
import com.example.talentbridge.resume.infrastructure.persistence.ResumeJpaEntity;
import com.example.talentbridge.user.domain.Gender;
import com.example.talentbridge.shared.infrastructure.persistence.BaseEntity;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@JsonPropertyOrder({"id", "name", "email", "password", "age", "address", "gender"})
public class UserJpaEntity extends BaseEntity {

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
    private CompanyJpaEntity company;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ResumeJpaEntity> resumes;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @ToString.Exclude
    private RoleJpaEntity role;

    public UserJpaEntity(String email, String name, String password, LocalDate dob, String address, Gender gender) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.dob = dob;
        this.address = address;
        this.gender = gender;
    }
}
