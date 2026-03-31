package com.metahrms.employee_management.dto.request.Leave;

import com.metahrms.employee_management.enums.Leave.ApprovalRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveRejectDto {

    @NotNull
    private Integer approverId;

    @NotNull
    private ApprovalRole approverRole;

    @NotBlank
    private String rejectReason;
}