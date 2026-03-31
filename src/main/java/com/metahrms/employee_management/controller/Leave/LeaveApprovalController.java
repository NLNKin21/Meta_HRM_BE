package com.metahrms.employee_management.controller.Leave;

import com.metahrms.employee_management.dto.request.Leave.LeaveApproveDto;
import com.metahrms.employee_management.dto.request.Leave.LeaveRejectDto;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.Leave.LeaveRequestResponseDto;
import com.metahrms.employee_management.service.Leave.LeaveApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leave-approvals")
@RequiredArgsConstructor
public class LeaveApprovalController {

    private final LeaveApprovalService leaveApprovalService;

    @GetMapping("/manager/{managerId}/pending")
    public ApiResponse<List<LeaveRequestResponseDto>> getPendingForManager(@PathVariable Integer managerId) {
        return ApiResponse.success(
                leaveApprovalService.getPendingForManager(managerId),
                "Lấy danh sách chờ duyệt cho trưởng phòng thành công"
        );
    }

    @GetMapping("/hr/{hrId}/pending")
    public ApiResponse<List<LeaveRequestResponseDto>> getPendingForHr(@PathVariable Integer hrId) {
        return ApiResponse.success(
                leaveApprovalService.getPendingForHr(hrId),
                "Lấy danh sách chờ duyệt cho HR thành công"
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