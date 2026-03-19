package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Integer>, JpaSpecificationExecutor<Position> {

    Optional<Position> findByIdAndIsDeletedFalse(Integer id);

    Optional<Position> findByPositionCodeAndIsDeletedFalse(String positionCode);

    List<Position> findByIsDeletedFalseAndIsActiveTrue();

    List<Position> findByDepartmentIdAndIsDeletedFalse(Integer departmentId);

    boolean existsByPositionCode(String positionCode);

    boolean existsByPositionName(String positionName);
}