package com.metahrms.employee_management.repository;

<<<<<<< HEAD
import com.metahrms.employee_management.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByIdAndIsDeletedFalse(Long id);

    Optional<Department> findByDeptCodeAndIsDeletedFalse(String deptCode);

    List<Department> findByIsDeletedFalseAndIsActiveTrue();

    List<Department> findByParentIdAndIsDeletedFalse(Long parentId);

    boolean existsByDeptCode(String deptCode);

    boolean existsByDeptName(String deptName);

    @Query("SELECT d FROM Department d WHERE d.isDeleted = false AND d.parent IS NULL")
    List<Department> findRootDepartments();

    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.id = :id AND d.isDeleted = false")
    Optional<Department> findByIdWithEmployees(Long id);
}
=======
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    List<Department> findByIsDeletedFalse();
    Optional<Department> findByDeptName(String deptName);

    // Dashboard queries
    Long countByIsDeleted(boolean isDeleted);

    List<Department> findByIsDeleted(boolean isDeleted);
}
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
