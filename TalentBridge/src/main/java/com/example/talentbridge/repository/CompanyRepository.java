package com.example.talentbridge.repository;

import com.example.talentbridge.model.Company;
import com.example.talentbridge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface CompanyRepository extends
        JpaRepository<Company, Long>,
        JpaSpecificationExecutor<Company> {
    Company findByOwnerEmail(String email);

    long countByCreatedAtBetween(Instant start, Instant end);
}
