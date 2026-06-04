package com.metahrms.employee_management.dto.response.Department;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Summary information about a department including employee count and manager details")
public class DepartmentSummaryDto {

    @Schema(description = "Unique identifier of the department", example = "1")
    private Integer id;

    @Schema(description = "Name of the department", example = "Phòng Nhân Sự")
    private String deptName;

    @Schema(description = "Name of the department manager", example = "Nguyễn Văn A")
    private String managerName;

    @Schema(description = "Total number of employees in the department", example = "25")
    private Long employeeCount;

    @Schema(description = "Department creation timestamp", example = "10/11/2025")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDateTime createdAt;

    // ✅ Thêm field isActive
    @Schema(description = "Department active status", example = "true")
    private Boolean isActive;
}