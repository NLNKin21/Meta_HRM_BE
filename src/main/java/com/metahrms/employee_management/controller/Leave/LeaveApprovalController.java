package com.metahrms.employee_management.controller.Leave;

import com.metahrms.employee_management.dto.request.Leave.LeaveApproveDto;
import com.metahrms.employee_management.dto.request.Leave.LeaveRejectDto;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.Leave.HrLeaveDashboardSummaryDto;
import com.metahrms.employee_management.dto.response.Leave.LeaveRequestResponseDto;
import com.metahrms.employee_management.dto.response.Leave.ManagerLeaveSummaryDto;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.service.Leave.LeaveApprovalService;
import com.metahrms.employee_management.util.SecurityUtils;

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
    private final EmployeeRepository employeeRepository;

    // ✅ THÊM: /manager/pending - lấy từ JWT
    @GetMapping("/manager/pending")
    public ApiResponse<List<LeaveRequestResponseDto>> getPendingForCurrentManager() {
        Integer userId = SecurityUtils.getCurrentUserId();
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

        return ApiResponse.success(
                leaveApprovalService.getPendingForManager(employee.getId()),
                "Lấy danh sách chờ duyệt cho trưởng phòng thành công"
        );
    }

    // ✅ Giữ lại endpoint cũ nếu cần (admin dùng)
    @GetMapping("/manager/{managerId}/pending")
    public ApiResponse<List<LeaveRequestResponseDto>> getPendingForManager(
            @PathVariable("managerId") Integer managerId) {
        return ApiResponse.success(
                leaveApprovalService.getPendingForManager(managerId),
                "Lấy danh sách chờ duyệt cho trưởng phòng thành công"
        );
    }

    // ✅ Đã có - giữ nguyên
    @GetMapping("/manager/summary")
    public ApiResponse<ManagerLeaveSummaryDto> getManagerSummary(
            @RequestParam(value = "startDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Integer userId = SecurityUtils.getCurrentUserId();
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return ApiResponse.success(
                leaveApprovalService.getManagerSummary(employee.getId(), startDate, endDate),
                "Lấy thống kê nghỉ phép cho trưởng phòng thành công"
        );
    }

    // ✅ Đã có - giữ nguyên
    @GetMapping("/manager/history")
    public ApiResponse<List<LeaveRequestResponseDto>> getManagerHistory(
            @RequestParam(value = "startDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Integer userId = SecurityUtils.getCurrentUserId();
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return ApiResponse.success(
                leaveApprovalService.getManagerHistory(employee.getId(), startDate, endDate),
                "Lấy lịch sử xử lý đơn của trưởng phòng thành công"
        );
    }

    @GetMapping("/hr/history")
    public ApiResponse<List<LeaveRequestResponseDto>> getHrHistory(
            @RequestParam(value = "startDate", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Integer userId = SecurityUtils.getCurrentUserId();
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return ApiResponse.success(
                leaveApprovalService.getHrHistory(employee.getId(), startDate, endDate),
                "Lấy lịch sử xử lý đơn của HR thành công"
        );
    }


    // ✅ THÊM: /hr/pending - lấy từ JWT
    @GetMapping("/hr/pending")
    public ApiResponse<List<LeaveRequestResponseDto>> getPendingForCurrentHr() {
        Integer userId = SecurityUtils.getCurrentUserId();
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

        return ApiResponse.success(
                leaveApprovalService.getPendingForHr(employee.getId()),
                "Lấy danh sách chờ duyệt cho HR thành công"
        );
    }

    // ✅ THÊM: /hr/dashboard-summary - lấy từ JWT
    @GetMapping("/hr/dashboard-summary")
    public ApiResponse<HrLeaveDashboardSummaryDto> getHrDashboardSummaryForCurrentHr() {
        Integer userId = SecurityUtils.getCurrentUserId();
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

        return ApiResponse.success(
                leaveApprovalService.getHrDashboardSummary(employee.getId()),
                "Lấy tổng quan nghỉ phép cho HR thành công"
        );
    }

    // ✅ Giữ lại endpoint cũ nếu cần (admin dùng)
    @GetMapping("/hr/{hrId}/pending")
    public ApiResponse<List<LeaveRequestResponseDto>> getPendingForHr(
            @PathVariable("hrId") Integer hrId) {
        return ApiResponse.success(
                leaveApprovalService.getPendingForHr(hrId),
                "Lấy danh sách chờ duyệt cho HR thành công"
        );
    }

    // ✅ Giữ lại endpoint cũ nếu cần (admin dùng)
    @GetMapping("/hr/{hrId}/dashboard-summary")
    public ApiResponse<HrLeaveDashboardSummaryDto> getHrDashboardSummary(
            @PathVariable("hrId") Integer hrId) {
        return ApiResponse.success(
                leaveApprovalService.getHrDashboardSummary(hrId),
                "Lấy tổng quan nghỉ phép cho HR thành công"
        );
    }

    @PutMapping("/{leaveRequestId}/approve")
    public ApiResponse<LeaveRequestResponseDto> approve(
            @PathVariable("leaveRequestId") Long leaveRequestId,
            @Valid @RequestBody LeaveApproveDto dto) {
        return ApiResponse.success(
                leaveApprovalService.approve(leaveRequestId, dto),
                "Duyệt đơn nghỉ thành công"
        );
    }

    @PutMapping("/{leaveRequestId}/reject")
    public ApiResponse<LeaveRequestResponseDto> reject(
            @PathVariable("leaveRequestId") Long leaveRequestId,
            @Valid @RequestBody LeaveRejectDto dto) {
        return ApiResponse.success(
                leaveApprovalService.reject(leaveRequestId, dto),
                "Từ chối đơn nghỉ thành công"
        );
    }
}