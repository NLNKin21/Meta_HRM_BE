package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    Optional<Position> findByIdAndIsDeletedFalse(Long id);

    Optional<Position> findByPositionCodeAndIsDeletedFalse(String positionCode);

    List<Position> findByIsDeletedFalseAndIsActiveTrue();

    List<Position> findByDepartmentIdAndIsDeletedFalse(Long departmentId);

    boolean existsByPositionCode(String positionCode);

    boolean existsByPositionName(String positionName);
}