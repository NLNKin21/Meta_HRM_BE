package com.metahrms.employee_management.service;

import com.metahrms.employee_management.dto.request.PermissionUpdateRequest;
import com.metahrms.employee_management.dto.response.PermissionResponse;
import com.metahrms.employee_management.entity.RolePermission;
import com.metahrms.employee_management.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final RolePermissionRepository repository;

    // ─── Danh sách module ────────────────────────────────
    private static final List<Map<String, Object>> ALL_MODULES = List.of(
        Map.of("key", "dashboard",        "name", "Dashboard",              "order", 1),
        Map.of("key", "users",            "name", "Account Management",     "order", 2),
        Map.of("key", "employees",        "name", "Danh sách nhân viên",    "order", 3),
        Map.of("key", "departments",      "name", "Phòng ban",              "order", 4),
        Map.of("key", "positions",        "name", "Chức vụ",                "order", 5),
        Map.of("key", "contracts",        "name", "Hợp đồng lao động",     "order", 6),
        Map.of("key", "leave-management", "name", "Quản lý nghỉ phép",     "order", 7),
        Map.of("key", "attendance",       "name", "Quản lý chấm công",     "order", 8),
        Map.of("key", "payroll",          "name", "Bảng lương",             "order", 9),
        Map.of("key", "help",            "name", "Trợ giúp",               "order", 10)
    );

    // ─── Mặc định cho HR ─────────────────────────────────
    private static final Set<String> HR_DEFAULTS = Set.of(
        "dashboard", "users", "employees", "departments", "positions",
        "contracts", "leave-management", "attendance", "help"
    );

    // ─── Mặc định cho ACCOUNTANT ─────────────────────────
    private static final Set<String> ACCOUNTANT_DEFAULTS = Set.of(
        "dashboard", "payroll", "help"
    );

    // ─── Khởi tạo dữ liệu mặc định ─────────────────────
    @PostConstruct
    public void initDefaultPermissions() {
        initRolePermissions("HR", HR_DEFAULTS);
        initRolePermissions("ACCOUNTANT", ACCOUNTANT_DEFAULTS);
    }

    private void initRolePermissions(String role, Set<String> defaults) {
        List<RolePermission> existing = repository.findByRoleOrderBySortOrderAsc(role);

        if (!existing.isEmpty()) return; // Đã có rồi thì không init lại

        for (Map<String, Object> module : ALL_MODULES) {
            String key = (String) module.get("key");
            String name = (String) module.get("name");
            int order = (int) module.get("order");

            repository.save(RolePermission.builder()
                    .role(role)
                    .moduleKey(key)
                    .moduleName(name)
                    .enabled(defaults.contains(key))
                    .sortOrder(order)
                    .build());
        }
    }

    // ─── Lấy tất cả permissions của 1 role ──────────────
    public List<PermissionResponse> getPermissionsByRole(String role) {
        return repository.findByRoleOrderBySortOrderAsc(role.toUpperCase())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── Lấy chỉ những module enabled ───────────────────
    public List<String> getEnabledModules(String role) {
        return repository.findByRoleAndEnabledTrueOrderBySortOrderAsc(role.toUpperCase())
                .stream()
                .map(RolePermission::getModuleKey)
                .collect(Collectors.toList());
    }

    // ─── Cập nhật permissions ────────────────────────────
    @Transactional
    public void updatePermissions(PermissionUpdateRequest request) {
        String role = request.getRole().toUpperCase();

        for (PermissionUpdateRequest.ModulePermission mp : request.getModules()) {
            repository.findByRoleAndModuleKey(role, mp.getModuleKey())
                    .ifPresent(perm -> {
                        perm.setEnabled(mp.getEnabled());
                        repository.save(perm);
                    });
        }
    }

    // ─── Reset về mặc định ──────────────────────────────
    @Transactional
    public void resetToDefault(String role) {
        String upperRole = role.toUpperCase();
        Set<String> defaults = "HR".equals(upperRole) ? HR_DEFAULTS : ACCOUNTANT_DEFAULTS;

        List<RolePermission> perms = repository.findByRoleOrderBySortOrderAsc(upperRole);
        for (RolePermission perm : perms) {
            perm.setEnabled(defaults.contains(perm.getModuleKey()));
            repository.save(perm);
        }
    }

    private PermissionResponse toResponse(RolePermission entity) {
        return PermissionResponse.builder()
                .id(entity.getId())
                .role(entity.getRole())
                .moduleKey(entity.getModuleKey())
                .moduleName(entity.getModuleName())
                .enabled(entity.getEnabled())
                .sortOrder(entity.getSortOrder())
                .build();
    }
}