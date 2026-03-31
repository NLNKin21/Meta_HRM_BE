package com.metahrms.employee_management.entity.Leave;

import com.metahrms.employee_management.enums.Leave.LeaveApprovalStage;
import com.metahrms.employee_management.enums.Leave.LeaveDurationType;
import com.metahrms.employee_management.enums.Leave.LeaveStatus;
import com.metahrms.employee_management.enums.Leave.LeaveUnit;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @Column(name = "employee_name", length = 255)
    private String employeeName;


    @Column(name = "manager_id")
    private Integer managerId;

    @Column(name = "hr_id")
    private Integer hrId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveUnit leaveUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveDurationType startSession;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveDurationType endSession;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalDays;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveApprovalStage approvalStage;

    @Column(columnDefinition = "TEXT")
    private String rejectReason;

    @Column(columnDefinition = "TEXT")
    private String cancelReason;

    @Column(nullable = false)
    private Boolean finalApproved;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime cancelledAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = LeaveStatus.DRAFT;
        if (this.approvalStage == null) this.approvalStage = LeaveApprovalStage.NONE;
        if (this.finalApproved == null) this.finalApproved = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}