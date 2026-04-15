package com.metahrms.employee_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    List<Department> findByIsDeletedFalse();

    Optional<Department> findByDeptName(String deptName);

    Optional<Department> findByDeptNameAndIsDeletedFalse(String deptName);

    /**
     * Tìm theo ID và chưa bị xoá
     */
    Optional<Department> findByIdAndIsDeletedFalse(Integer id);

}