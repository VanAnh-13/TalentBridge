package com.example.talentbridge.subscriber.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriberJpaRepository extends
        JpaRepository<SubscriberJpaEntity, Long>,
        JpaSpecificationExecutor<SubscriberJpaEntity> {

    Optional<SubscriberJpaEntity> findByEmail(String email);
}
