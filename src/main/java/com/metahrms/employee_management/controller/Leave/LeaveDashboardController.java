package com.metahrms.employee_management.controller.Leave;

import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.Leave.ManagerLeaveSummaryDto;
import com.metahrms.employee_management.service.Leave.LeaveDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leave-dashboard")
@RequiredArgsConstructor
public class LeaveDashboardController {

    private final LeaveDashboardService leaveDashboardService;

    @GetMapping("/manager-summary/{managerId}")
    public ApiResponse<ManagerLeaveSummaryDto> getManagerSummary(@PathVariable Integer managerId) {
        return ApiResponse.success(
                leaveDashboardService.getManagerSummary(managerId),
                "Lấy thống kê đơn nghỉ của manager thành công"
        );
    }
}