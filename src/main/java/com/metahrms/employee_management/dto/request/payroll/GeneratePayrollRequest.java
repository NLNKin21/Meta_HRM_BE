package com.metahrms.employee_management.dto.request.payroll;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePayrollRequest {

    @NotNull(message = "Month is required")
    @Min(1) @Max(12)
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(2020)
    private Integer year;

    /**
     * Null = tạo cho tất cả NV active
     * Có giá trị = chỉ tạo cho các NV trong danh sách
     */
    private List<Integer> employeeIds;
}