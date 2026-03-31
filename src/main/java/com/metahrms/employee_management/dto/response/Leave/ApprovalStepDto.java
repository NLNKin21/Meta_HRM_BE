package com.metahrms.employee_management.dto.response.Leave;

import com.metahrms.employee_management.enums.Leave.ApprovalAction;
import com.metahrms.employee_management.enums.Leave.ApprovalRole;
import com.metahrms.employee_management.enums.Leave.LeaveApprovalStage;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApprovalStepDto {
    private Integer actorId;
    private ApprovalRole actorRole;
    private ApprovalAction action;
    private LeaveApprovalStage stage;
    private String note;
    private LocalDateTime actionAt;
}