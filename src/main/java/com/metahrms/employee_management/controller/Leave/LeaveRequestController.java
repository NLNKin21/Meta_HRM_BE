package com.metahrms.employee_management.controller.Leave;

import com.metahrms.employee_management.dto.request.Leave.*;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.Leave.LeaveCalendarItemDto;
import com.metahrms.employee_management.dto.response.Leave.LeaveRequestResponseDto;
import com.metahrms.employee_management.service.Leave.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @PostMapping("/draft")
    public ApiResponse<LeaveRequestResponseDto> createDraft(@Valid @RequestBody LeaveRequestCreateDto dto) {
        return ApiResponse.success(leaveRequestService.createDraft(dto), "Tạo nháp đơn nghỉ thành công");
    }

    @PutMapping("/{id}/draft")
    public ApiResponse<LeaveRequestResponseDto> updateDraft(
            @PathVariable("id") Long id,
            @Valid @RequestBody LeaveRequestUpdateDraftDto dto
    ) {
        return ApiResponse.success(leaveRequestService.updateDraft(id, dto), "Cập nhật nháp thành công");
    }

    @PutMapping("/{id}/submit")
    public ApiResponse<LeaveRequestResponseDto> submit(@PathVariable("id") Long id, @Valid @RequestBody LeaveSubmitDto dto) {
        return ApiResponse.success(leaveRequestService.submit(id, dto), "Gửi đơn nghỉ thành công");
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<LeaveRequestResponseDto> cancel(@PathVariable("id") Long id, @Valid @RequestBody LeaveCancelDto dto) {
        return ApiResponse.success(leaveRequestService.cancel(id, dto), "Hủy đơn nghỉ thành công");
    }

    @GetMapping("/{id}")
    public ApiResponse<LeaveRequestResponseDto> getById(@PathVariable("id") Long id) {
        return ApiResponse.success(leaveRequestService.getById(id), "Lấy chi tiết đơn nghỉ thành công");
    }

    @GetMapping("/employee/{employeeId}")
    public ApiResponse<List<LeaveRequestResponseDto>> getByEmployee(@PathVariable("employeeId") Integer employeeId) {
        return ApiResponse.success(leaveRequestService.getByEmployee(employeeId), "Lấy danh sách đơn nghỉ thành công");
    }

    @GetMapping("/calendar")
    public ApiResponse<List<LeaveCalendarItemDto>> getCalendar(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate
    ) {
        return ApiResponse.success(leaveRequestService.getCalendar(startDate, endDate), "Lấy lịch nghỉ thành công");
    }
}