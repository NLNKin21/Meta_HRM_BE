package com.metahrms.employee_management.controller.Leave;

import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.Leave.LeaveBalanceResponseDto;
import com.metahrms.employee_management.dto.response.Leave.LeaveRequestResponseDto;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.service.Leave.LeaveBalanceService;
import com.metahrms.employee_management.service.Leave.LeaveRequestService;
import com.metahrms.employee_management.util.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveRequestService leaveRequestService;
    private final LeaveBalanceService leaveBalanceService;
    private final EmployeeRepository employeeRepository;

    /**
     * GET /leaves/my
     * Lấy tất cả đơn nghỉ của employee đang đăng nhập
     */
    @GetMapping("/my")
    public ApiResponse<List<LeaveRequestResponseDto>> getMyLeaves() {
        log.info("🚀 GET /leaves/my");
        
        Integer employeeId = getCurrentEmployeeId();
        log.info("📌 Employee ID: {}", employeeId);
        
        List<LeaveRequestResponseDto> leaves = leaveRequestService.getByEmployee(employeeId);
        
        return ApiResponse.<List<LeaveRequestResponseDto>>builder()
            .code(200)
            .status("success")
            .message("Lấy danh sách đơn nghỉ thành công")
            .data(leaves)
            .build();
    }

    @GetMapping("/balance")
    public ApiResponse<List<LeaveBalanceResponseDto>> getMyBalance(
        @RequestParam(name="year", required = false) Integer year
    ) {
        log.info("🚀 GET /leaves/balance?year={}", year);
        
        Integer employeeId = getCurrentEmployeeId();
        Integer currentYear = year != null ? year : LocalDate.now().getYear();
        
        log.info("📌 Employee ID: {}, Year: {}", employeeId, currentYear);
        
        List<LeaveBalanceResponseDto> balances = 
            leaveBalanceService.getEmployeeBalances(employeeId, currentYear);
        
        return ApiResponse.<List<LeaveBalanceResponseDto>>builder()
            .code(200)
            .status("success")
            .message("Lấy số dư nghỉ phép thành công")
            .data(balances)
            .build();
    }

    /**
     * ✅ Lấy employeeId từ userId trong SecurityContext
     * Logic giống hệt EmployeeService.getCurrentUserEmployee()
     */
    private Integer getCurrentEmployeeId() {
        // ✅ Sử dụng SecurityUtils như EmployeeService đã làm
        Integer currentUserId = SecurityUtils.getCurrentUserId();
        
        if (currentUserId == null) {
            log.error("❌ User not authenticated");
            throw new IllegalStateException("Người dùng chưa đăng nhập");
        }

        log.info("👤 Current user ID: {}", currentUserId);

        // ✅ Query Employee theo userId
        Employee employee = employeeRepository.findByUserId(currentUserId)
            .orElseThrow(() -> {
                log.error("❌ Employee not found for userId: {}", currentUserId);
                return new ResourceNotFoundException("Không tìm thấy nhân viên");
            });

        // ✅ Kiểm tra employee có bị xóa không
        if (Boolean.TRUE.equals(employee.getIsDeleted())) {
            log.error("❌ Employee has been deleted: {}", employee.getId());
            throw new ResourceNotFoundException("Nhân viên đã bị xóa");
        }

        Integer empId = employee.getId(); // Lấy primary key của Employee
        log.info("✅ Found employee ID: {}", empId);
        
        return empId;
    }
}