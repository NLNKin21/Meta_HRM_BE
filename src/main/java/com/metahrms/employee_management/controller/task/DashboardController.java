package com.metahrms.employee_management.controller.task;

import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.task.taskstatus.TaskStatsResponse;
import com.metahrms.employee_management.service.task.TaskStatsService;
import com.metahrms.employee_management.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "APIs for dashboard statistics")
public class DashboardController {

    private final TaskStatsService statsService;

    /**
     * GET /api/dashboard/stats
     * Lấy thống kê tổng quan cho Dashboard
     */
    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<ApiResponse<TaskStatsResponse>> getDashboardStats(
            @Parameter(description = "Department ID (optional)")
            @RequestParam(value = "departmentId", required = false) Integer departmentId) {

        Integer userId = SecurityUtils.getCurrentUserId();

        TaskStatsResponse stats = statsService.getDashboardStats(userId, departmentId);
        return ResponseEntity.ok(
                ApiResponse.success(stats, "Retrieved dashboard statistics successfully")
        );
    }

    /**
     * GET /api/dashboard/stats/department/{departmentId}
     * Lấy thống kê theo department
     */
    @GetMapping("/stats/department/{departmentId}")
    @Operation(summary = "Get department statistics", 
               description = "Returns statistics for specific department")
    public ResponseEntity<ApiResponse<TaskStatsResponse>> getStatsByDepartment(
            @Parameter(description = "Department ID", required = true)
            @PathVariable("departmentId") Integer departmentId) {
        
        TaskStatsResponse stats = statsService.getStatsByDepartment(departmentId);
        return ResponseEntity.ok(
            ApiResponse.success(stats, "Retrieved department statistics successfully")
        );
    }
}