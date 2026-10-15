package com.example.talentbridge.shared.infrastructure.messaging;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, Long> {

    @Query("select e from OutboxEventJpaEntity e where e.publishedAt is null order by e.id")
    List<OutboxEventJpaEntity> findPending(Pageable page);

    long countByPublishedAtIsNull();

    @Modifying
    @Query("delete from OutboxEventJpaEntity e where e.publishedAt is not null and e.publishedAt < :before")
    int deletePublishedBefore(@Param("before") Instant before);
}
