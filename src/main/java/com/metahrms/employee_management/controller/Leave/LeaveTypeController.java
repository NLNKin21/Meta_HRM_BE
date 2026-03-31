package com.metahrms.employee_management.controller.Leave;

import com.metahrms.employee_management.dto.request.Leave.LeaveTypeCreateDto;
import com.metahrms.employee_management.dto.request.Leave.LeaveTypeUpdateDto;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.Leave.LeaveTypeResponseDto;
import com.metahrms.employee_management.service.Leave.LeaveTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leave-types")
@RequiredArgsConstructor
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    @PostMapping
    public ApiResponse<LeaveTypeResponseDto> create(@Valid @RequestBody LeaveTypeCreateDto dto) {
        return ApiResponse.success(leaveTypeService.create(dto), "Tạo loại nghỉ thành công");
    }

    @PutMapping("/{id}")
    public ApiResponse<LeaveTypeResponseDto> update(@PathVariable Long id, @Valid @RequestBody LeaveTypeUpdateDto dto) {
        return ApiResponse.success(leaveTypeService.update(id, dto), "Cập nhật loại nghỉ thành công");
    }

    @GetMapping("/{id}")
    public ApiResponse<LeaveTypeResponseDto> getById(@PathVariable Long id) {
        return ApiResponse.success(leaveTypeService.getById(id), "Lấy chi tiết loại nghỉ thành công");
    }

    @GetMapping
    public ApiResponse<List<LeaveTypeResponseDto>> getAll() {
        return ApiResponse.success(leaveTypeService.getAll(), "Lấy danh sách loại nghỉ thành công");
    }
}