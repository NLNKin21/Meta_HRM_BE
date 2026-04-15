package com.metahrms.employee_management.dto.request.shift;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignShiftRequest {

    @NotNull(message = "Shift ID is required")
    private Integer shiftId;

    /**
     * Danh sách employee IDs cần gán ca
     * Nếu null/empty → dùng cho endpoint gán 1 employee
     */
    private List<Integer> employeeIds;
}