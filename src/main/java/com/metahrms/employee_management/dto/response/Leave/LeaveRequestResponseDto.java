package com.metahrms.employee_management.dto.response.Leave;

import com.metahrms.employee_management.enums.Leave.LeaveApprovalStage;
import com.metahrms.employee_management.enums.Leave.LeaveDurationType;
import com.metahrms.employee_management.enums.Leave.LeaveStatus;
import com.metahrms.employee_management.enums.Leave.LeaveUnit;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class LeaveRequestResponseDto {
    private Long id;
    private Integer employeeId;
    private String employeeName;
    private Integer managerId;
    private Integer hrId;
    private Long leaveTypeId;
    private String leaveTypeCode;
    private String leaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveUnit leaveUnit;
    private LeaveDurationType startSession;
    private LeaveDurationType endSession;
    private BigDecimal totalDays;
    private String reason;
    private LeaveStatus status;
    private LeaveApprovalStage approvalStage;
    private String rejectReason;
    private String cancelReason;
    private Boolean finalApproved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;
    private List<String> attachmentUrls;
    private List<ApprovalStepDto> approvalSteps;
    private String managerName;
    private String departmentName;
}