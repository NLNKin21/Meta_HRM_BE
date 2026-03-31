package com.metahrms.employee_management.dto.request.Leave;

import com.metahrms.employee_management.enums.Leave.ApprovalRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class LeaveBulkActionDto {

    @NotNull
    private Integer approverId;

    @NotNull
    private ApprovalRole approverRole;

    @NotNull
    private List<Long> leaveRequestIds;

    private String note;
    private String rejectReason;
}