package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRoleOrderBySortOrderAsc(String role);

    List<RolePermission> findByRoleAndEnabledTrueOrderBySortOrderAsc(String role);

    Optional<RolePermission> findByRoleAndModuleKey(String role, String moduleKey);

    void deleteByRole(String role);
}