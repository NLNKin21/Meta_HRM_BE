package com.metahrms.employee_management.controller.Leave;

import com.metahrms.employee_management.dto.request.Leave.LeaveApproveDto;
import com.metahrms.employee_management.dto.request.Leave.LeaveRejectDto;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.Leave.HrLeaveDashboardSummaryDto;
import com.metahrms.employee_management.dto.response.Leave.LeaveRequestResponseDto;
import com.metahrms.employee_management.dto.response.Leave.ManagerLeaveSummaryDto;
import com.metahrms.employee_management.service.Leave.LeaveApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/leave-approvals")
@RequiredArgsConstructor
public class LeaveApprovalController {

    private final LeaveApprovalService leaveApprovalService;

    @GetMapping("/manager/{managerId}/pending")
    public ApiResponse<List<LeaveRequestResponseDto>> getPendingForManager(
            @PathVariable Integer managerId
    ) {
        return ApiResponse.success(
                leaveApprovalService.getPendingForManager(managerId),
                "Lấy danh sách chờ duyệt cho trưởng phòng thành công"
        );
    }

    @GetMapping("/manager/{managerId}/summary")
    public ApiResponse<ManagerLeaveSummaryDto> getManagerSummary(
            @PathVariable Integer managerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ApiResponse.success(
                leaveApprovalService.getManagerSummary(managerId, startDate, endDate),
                "Lấy thống kê nghỉ phép cho trưởng phòng thành công"
        );
    }

    @GetMapping("/manager/{managerId}/history")
    public ApiResponse<List<LeaveRequestResponseDto>> getManagerHistory(
            @PathVariable Integer managerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ApiResponse.success(
                leaveApprovalService.getManagerHistory(managerId, startDate, endDate),
                "Lấy lịch sử xử lý đơn của trưởng phòng thành công"
        );
    }

    @GetMapping("/hr/{hrId}/pending")
    public ApiResponse<List<LeaveRequestResponseDto>> getPendingForHr(
            @PathVariable Integer hrId
    ) {
        return ApiResponse.success(
                leaveApprovalService.getPendingForHr(hrId),
                "Lấy danh sách chờ duyệt cho HR thành công"
        );
    }

    @GetMapping("/hr/{hrId}/dashboard-summary")
    public ApiResponse<HrLeaveDashboardSummaryDto> getHrDashboardSummary(
            @PathVariable Integer hrId
    ) {
        return ApiResponse.success(
                leaveApprovalService.getHrDashboardSummary(hrId),
                "Lấy tổng quan nghỉ phép cho HR thành công"
        );
    }

    @PutMapping("/{leaveRequestId}/approve")
    public ApiResponse<LeaveRequestResponseDto> approve(
            @PathVariable Long leaveRequestId,
            @Valid @RequestBody LeaveApproveDto dto
    ) {
        return ApiResponse.success(
                leaveApprovalService.approve(leaveRequestId, dto),
                "Duyệt đơn nghỉ thành công"
        );
    }

    @PutMapping("/{leaveRequestId}/reject")
    public ApiResponse<LeaveRequestResponseDto> reject(
            @PathVariable Long leaveRequestId,
            @Valid @RequestBody LeaveRejectDto dto
    ) {
        return ApiResponse.success(
                leaveApprovalService.reject(leaveRequestId, dto),
                "Từ chối đơn nghỉ thành công"
        );
    }
}