package com.metahrms.employee_management.controller.Leave;

import com.metahrms.employee_management.dto.request.Leave.LeaveBalanceInitDto;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.Leave.LeaveBalanceResponseDto;
import com.metahrms.employee_management.service.Leave.LeaveBalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leave-balances")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    @PostMapping("/init")
    public ApiResponse<LeaveBalanceResponseDto> init(@Valid @RequestBody LeaveBalanceInitDto dto) {
        return ApiResponse.success(
                leaveBalanceService.initBalance(dto),
                "Khởi tạo balance thành công"
        );
    }

    @GetMapping
    public ApiResponse<List<LeaveBalanceResponseDto>> getBalances(
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("year") Integer year
    ) {
        return ApiResponse.success(
                leaveBalanceService.getEmployeeBalances(employeeId, year),
                "Lấy số dư nghỉ phép thành công"
        );
    }

    @PostMapping("/sync")
    public ApiResponse<String> syncBalancesForYear(@RequestParam Integer year) {
        leaveBalanceService.syncBalancesForYear(year);
        return ApiResponse.success(
                "OK",
                "Đồng bộ balance cho toàn bộ nhân viên thành công"
        );
    }

    @PostMapping("/sync/leave-type/{leaveTypeId}")
    public ApiResponse<String> syncBalancesForLeaveType(
            @PathVariable Long leaveTypeId,
            @RequestParam Integer year
    ) {
        leaveBalanceService.syncBalancesForLeaveType(leaveTypeId, year);
        return ApiResponse.success(
                "OK",
                "Đồng bộ balance theo loại nghỉ thành công"
        );
    }

    @PostMapping("/sync/employee/{employeeId}")
    public ApiResponse<String> initBalancesForEmployee(
            @PathVariable Integer employeeId,
            @RequestParam Integer year
    ) {
        leaveBalanceService.initBalancesForEmployee(employeeId, year);
        return ApiResponse.success(
                "OK",
                "Khởi tạo balance cho nhân viên thành công"
        );
    }
}