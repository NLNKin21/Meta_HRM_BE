package com.metahrms.employee_management.entity.CV;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.metahrms.employee_management.enums.CandidateStatus;

@Entity
@Table(name = "recruitment_histories", indexes = {
    @Index(name = "idx_recruitment_histories_candidate_id", columnList = "candidate_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 30)
    private CandidateStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", length = 30)
    private CandidateStatus toStatus;

    @Column(name = "performed_by")
    private Integer performedBy;

    @Column(name = "performed_by_name", length = 150)
    private String performedByName;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
