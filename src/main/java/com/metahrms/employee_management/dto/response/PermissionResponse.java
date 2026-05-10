package com.metahrms.employee_management.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse {
    private Long id;
    private String role;
    private String moduleKey;
    private String moduleName;
    private Boolean enabled;
    private Integer sortOrder;
}