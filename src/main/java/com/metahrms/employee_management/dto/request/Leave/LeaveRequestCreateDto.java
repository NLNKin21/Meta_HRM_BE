package com.metahrms.employee_management.dto.request.Leave;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.metahrms.employee_management.enums.Leave.LeaveDurationType;
import com.metahrms.employee_management.enums.Leave.LeaveUnit;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class LeaveRequestCreateDto {

    @NotNull
    private Integer employeeId;

    
    private Integer managerId;

    private Integer hrId;

    @NotNull
    private Long leaveTypeId;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull
    private LeaveUnit leaveUnit;

    @NotNull
    private LeaveDurationType startSession;

    @NotNull
    private LeaveDurationType endSession;

    @NotBlank
    @Size(min = 10, message = "Lý do nghỉ tối thiểu 10 ký tự")
    private String reason;

    private List<AttachmentInput> attachments;

    @Data
    public static class AttachmentInput {
        @NotBlank
        private String fileName;
        @NotBlank
        private String fileUrl;
        @NotBlank
        private String fileType;
        @NotNull
        private Long fileSize;
    }
}