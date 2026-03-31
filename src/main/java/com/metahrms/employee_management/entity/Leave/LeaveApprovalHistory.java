package com.metahrms.employee_management.entity.Leave;

import com.metahrms.employee_management.enums.Leave.ApprovalAction;
import com.metahrms.employee_management.enums.Leave.ApprovalRole;
import com.metahrms.employee_management.enums.Leave.LeaveApprovalStage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "leave_approval_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "leave_request_id", nullable = false)
    private Long leaveRequestId;

    @Column(nullable = false)
    private Integer actorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_role", nullable = false)
    private ApprovalRole actorRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalAction action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveApprovalStage stage;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    private LocalDateTime actionAt;

    @PrePersist
    public void prePersist() {
        this.actionAt = LocalDateTime.now();
    }
}