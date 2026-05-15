package com.metahrms.employee_management.entity.CV;

import com.metahrms.employee_management.entity.BaseEntity;
import com.metahrms.employee_management.enums.InterviewResult;
import com.metahrms.employee_management.enums.InterviewType;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "interviews", indexes = {
    @Index(name = "idx_interviews_candidate_id", columnList = "candidate_id"),
    @Index(name = "idx_interviews_date", columnList = "interview_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview extends BaseEntity {

    // ========== LIÊN KẾT ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    // ========== LỊCH PHỎNG VẤN ==========

    @Column(name = "interview_date", nullable = false)
    private LocalDateTime interviewDate;

    @Column(name = "duration_minutes")
    @Builder.Default
    private Integer durationMinutes = 60;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_type", nullable = false, length = 20)
    @Builder.Default
    private InterviewType interviewType = InterviewType.OFFLINE;

    @Column(name = "location", length = 500)
    private String location;

    // ========== NGƯỜI PHỎNG VẤN ==========

    @Column(name = "interviewer_id", nullable = false)
    private Integer interviewerId;

    @Column(name = "interviewer_name", length = 150)
    private String interviewerName;

    // ========== KẾT QUẢ ==========

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 20)
    @Builder.Default
    private InterviewResult result = InterviewResult.PENDING;

    @Column(name = "score")
    private Integer score;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths;

    @Column(name = "weaknesses", columnDefinition = "TEXT")
    private String weaknesses;

    // ========== META ==========

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "email_sent")
    @Builder.Default
    private Boolean emailSent = false;

    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;
}