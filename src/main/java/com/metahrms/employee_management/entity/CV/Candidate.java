package com.metahrms.employee_management.entity.CV;

import com.metahrms.employee_management.entity.BaseEntity;
import com.metahrms.employee_management.enums.CandidateStatus;
import com.metahrms.employee_management.enums.Gender;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidates", indexes = {
    @Index(name = "idx_candidates_email", columnList = "email"),
    @Index(name = "idx_candidates_status", columnList = "status"),
    @Index(name = "idx_candidates_applied_at", columnList = "applied_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate extends BaseEntity {

    // ========== THÔNG TIN CÁ NHÂN ==========

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "dob")
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Column(name = "address", length = 255)
    private String address;

    // ========== VỊ TRÍ ỨNG TUYỂN ==========

    @Column(name = "desired_position", nullable = false, length = 200)
    private String desiredPosition;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "expected_salary", length = 50)
    private String expectedSalary;

    // ========== HỒ SƠ ==========

    @Column(name = "cv_file_url", length = 500)
    private String cvFileUrl;

    @Column(name = "cv_file_key", length = 255)
    private String cvFileKey;

    @Column(name = "cv_file_name", length = 255)
    private String cvFileName;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    // ========== TRẠNG THÁI ==========

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private CandidateStatus status = CandidateStatus.NEW;

    @Column(name = "applied_at", nullable = false)
    @Builder.Default
    private LocalDateTime appliedAt = LocalDateTime.now();

    // ========== KẾT QUẢ XỬ LÝ ==========

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    // ========== LIÊN KẾT SAU KHI DUYỆT ==========

    @Column(name = "created_user_id")
    private Integer createdUserId;

    @Column(name = "created_employee_id")
    private Integer createdEmployeeId;

    // ========== HR XỬ LÝ ==========

    @Column(name = "reviewed_by")
    private Integer reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "approved_by")
    private Integer approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}