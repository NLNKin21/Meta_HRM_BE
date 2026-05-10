package com.metahrms.employee_management.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class PermissionUpdateRequest {
    private String role;
    private List<ModulePermission> modules;

    @Data
    public static class ModulePermission {
        private String moduleKey;
        private Boolean enabled;
    }
}