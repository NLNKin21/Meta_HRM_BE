package com.metahrms.employee_management.controller;

import com.metahrms.employee_management.dto.request.PermissionUpdateRequest;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.PermissionResponse;
import com.metahrms.employee_management.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Permissions", description = "API phân quyền module")
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "Lấy tất cả permissions theo role")
    @GetMapping("/{role}")
    public ApiResponse<List<PermissionResponse>> getByRole(@PathVariable("role") String role) {
        List<PermissionResponse> permissions = permissionService.getPermissionsByRole(role);
        return ApiResponse.<List<PermissionResponse>>builder()
                .status("success")
                .message("Lấy phân quyền thành công")
                .data(permissions)
                .build();
    }

    @Operation(summary = "Lấy danh sách module được bật cho role")
    @GetMapping("/{role}/enabled")
    public ApiResponse<List<String>> getEnabledModules(@PathVariable("role") String role) {
        List<String> modules = permissionService.getEnabledModules(role);
        return ApiResponse.<List<String>>builder()
                .status("success")
                .message("Lấy module thành công")
                .data(modules)
                .build();
    }

    @Operation(summary = "Cập nhật phân quyền")
    @PutMapping
    public ApiResponse<Void> updatePermissions(@RequestBody PermissionUpdateRequest request) {
        permissionService.updatePermissions(request);
        return ApiResponse.<Void>builder()
                .status("success")
                .message("Cập nhật phân quyền thành công")
                .build();
    }

    @Operation(summary = "Reset phân quyền về mặc định")
    @PostMapping("/{role}/reset")
    public ApiResponse<Void> resetToDefault(@PathVariable("role") String role) {
        permissionService.resetToDefault(role);
        return ApiResponse.<Void>builder()
                .status("success")
                .message("Reset phân quyền thành công")
                .build();
    }
}
