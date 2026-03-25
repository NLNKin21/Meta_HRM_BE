package com.metahrms.employee_management.repository.Task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Task.Project;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    // Tìm theo code
    Optional<Project> findByProjectCode(String projectCode);

    // Tìm theo department
    @Query("SELECT p FROM Project p WHERE p.department.id = :deptId AND p.isActive = true")
    List<Project> findByDepartmentId(@Param("deptId") Integer deptId);

    // Tìm theo manager
    @Query("SELECT p FROM Project p WHERE p.manager.id = :managerId AND p.isActive = true")
    List<Project> findByManagerId(@Param("managerId") Integer managerId);

    // Tìm theo status
    @Query("SELECT p FROM Project p WHERE p.status = :status AND p.isActive = true ORDER BY p.createdAt DESC")
    List<Project> findByStatus(@Param("status") String status);

    // Check code đã tồn tại
    boolean existsByProjectCode(String projectCode);

    // Đếm số project của department
    @Query("SELECT COUNT(p) FROM Project p WHERE p.department.id = :deptId AND p.isActive = true")
    Long countByDepartmentId(@Param("deptId") Integer deptId);

    // Lấy projects active
    @Query("SELECT p FROM Project p WHERE p.isActive = true ORDER BY p.createdAt DESC")
    List<Project> findAllActive();
}