// package com.metahrms.employee_management.entity;

// import com.metahrms.employee_management.enums.LeaveStatus;
// import com.metahrms.employee_management.enums.LeaveType;
// import jakarta.persistence.*;
// import lombok.*;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.temporal.ChronoUnit;

// @Entity
// @Table(name = "leave_requests", indexes = {
//     @Index(name = "idx_leave_employee", columnList = "employee_id"),
//     @Index(name = "idx_leave_status", columnList = "status"),
//     @Index(name = "idx_leave_dates", columnList = "start_date, end_date")
// })
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class LeaveRequest extends BaseEntity {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "employee_id", nullable = false)
//     private Employee employee;

//     @Enumerated(EnumType.STRING)
//     @Column(name = "leave_type", nullable = false, length = 20)
//     private LeaveType leaveType;

//     @Column(name = "start_date", nullable = false)
//     private LocalDate startDate;

//     @Column(name = "end_date", nullable = false)
//     private LocalDate endDate;

//     @Column(name = "total_days")
//     private Integer totalDays;

//     @Column(nullable = false, columnDefinition = "TEXT")
//     private String reason;

//     @Enumerated(EnumType.STRING)
//     @Column(nullable = false, length = 20)
//     private LeaveStatus status = LeaveStatus.PENDING;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "approved_by")
//     private User approvedBy;

//     @Column(name = "approved_at")
//     private LocalDateTime approvedAt;

//     @Column(name = "reject_reason", columnDefinition = "TEXT")
//     private String rejectReason;

//     @Column(name = "attachment_url")
//     private String attachmentUrl;

//     // ==================== BUSINESS METHODS ====================
    
//     @PrePersist
//     @PreUpdate
//     public void calculateTotalDays() {
//         if (startDate != null && endDate != null) {
//             this.totalDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
//         }
//     }
// }