package com.example.talentbridge.interview.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interviews")
@Getter
@Setter
@NoArgsConstructor
public class InterviewJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resume_id", nullable = false)
    private Long resumeId;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "recruiter_id", nullable = false)
    private Long recruiterId;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(nullable = false, length = 20)
    private String mode;

    @Column(nullable = false, length = 500)
    private String location;

    @Column(length = 1000)
    private String note;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "interview_slots", joinColumns = @JoinColumn(name = "interview_id"))
    @Column(name = "slot", nullable = false)
    @OrderBy
    private List<Instant> proposedSlots = new ArrayList<>();

    @Column(name = "confirmed_slot")
    private Instant confirmedSlot;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "reminded_24h", nullable = false)
    private boolean reminded24h;

    @Column(name = "reminded_1h", nullable = false)
    private boolean reminded1h;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
