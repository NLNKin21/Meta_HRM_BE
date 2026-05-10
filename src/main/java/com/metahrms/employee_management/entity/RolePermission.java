package com.metahrms.employee_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role_permissions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"role", "module_key"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String role;   // HR, ACCOUNTANT

    @Column(name = "module_key", nullable = false, length = 50)
    private String moduleKey;  // dashboard, users, employees...

    @Column(name = "module_name", nullable = false, length = 100)
    private String moduleName;  // Tên hiển thị

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = false;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
}