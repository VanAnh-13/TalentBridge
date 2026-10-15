package com.example.talentbridge.company.infrastructure.persistence;

import com.example.talentbridge.model.Job;
import com.example.talentbridge.model.User;
import com.example.talentbridge.shared.infrastructure.persistence.BaseEntity;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "companies")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@JsonPropertyOrder({"id", "name", "description", "address", "logoUrl"})
public class CompanyJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private String address;

    @OneToOne(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private CompanyLogoJpaEntity companyLogo;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<User> users;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Job> jobs;

    @OneToOne
    @JoinColumn(name = "owner_id", unique = true)
    private User owner;

    public CompanyJpaEntity(String name, String description, String address) {
        this.name = name;
        this.description = description;
        this.address = address;
    }
}
