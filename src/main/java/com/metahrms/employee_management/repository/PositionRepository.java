package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Integer>, JpaSpecificationExecutor<Position> {

    Optional<Position> findByIdAndIsDeletedFalse(Integer id);
    
    boolean existsByPositionCode(String positionCode);

    /**
     * Tìm tất cả root positions (không có parent) - WITH EAGER LOADING
     */
    @Query("SELECT DISTINCT p FROM Position p " +
           "LEFT JOIN FETCH p.department " +
           "LEFT JOIN FETCH p.parentPosition " +
           "WHERE p.parentPosition IS NULL AND p.isDeleted = false " +
           "ORDER BY p.sortOrder")
    List<Position> findByParentPositionIsNullAndIsDeletedFalseOrderBySortOrder();

    /**
     * Tìm root positions theo department - WITH EAGER LOADING
     */
    @Query("SELECT DISTINCT p FROM Position p " +
           "LEFT JOIN FETCH p.department " +
           "LEFT JOIN FETCH p.parentPosition " +
           "WHERE p.department.id = :departmentId " +
           "AND p.parentPosition IS NULL " +
           "AND p.isDeleted = false " +
           "ORDER BY p.sortOrder")
    List<Position> findByDepartmentIdAndParentPositionIsNullAndIsDeletedFalseOrderBySortOrder(
        @Param("departmentId") Integer departmentId
    );

    /**
     * Tìm children của một position - WITH EAGER LOADING
     */
    @Query("SELECT DISTINCT p FROM Position p " +
           "LEFT JOIN FETCH p.department " +
           "LEFT JOIN FETCH p.parentPosition " +
           "WHERE p.parentPosition.id = :parentPositionId " +
           "AND p.isDeleted = false " +
           "ORDER BY p.sortOrder")
    List<Position> findByParentPositionIdAndIsDeletedFalseOrderBySortOrder(
        @Param("parentPositionId") Integer parentPositionId
    );

    /**
     * Tìm theo department (tất cả levels) - WITH EAGER LOADING
     */
    @Query("SELECT DISTINCT p FROM Position p " +
           "LEFT JOIN FETCH p.department " +
           "LEFT JOIN FETCH p.parentPosition " +
           "WHERE p.department.id = :departmentId " +
           "AND p.isDeleted = false " +
           "ORDER BY p.levelOrder ASC, p.sortOrder ASC")
    List<Position> findByDepartmentIdAndIsDeletedFalseOrderByLevelOrderAscSortOrderAsc(
        @Param("departmentId") Integer departmentId
    );

    /**
     * Lấy tất cả positions với eager loading
     */
    @Query("SELECT DISTINCT p FROM Position p " +
           "LEFT JOIN FETCH p.department " +
           "LEFT JOIN FETCH p.parentPosition " +
           "WHERE p.isDeleted = false")
    List<Position> findAllWithRelations();

    /**
     * Đếm số nhân viên theo position
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.position.id = :positionId AND e.isDeleted = false")
    Long countEmployeesByPositionId(@Param("positionId") Integer positionId);

    /**
     * Kiểm tra position code tồn tại (exclude id khi update)
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Position p " +
           "WHERE p.positionCode = :code AND p.id <> :excludeId AND p.isDeleted = false")
    boolean existsByPositionCodeAndIdNot(@Param("code") String code, @Param("excludeId") Integer excludeId);

    /**
     * Lấy position by ID với eager loading
     */
    @Query("SELECT DISTINCT p FROM Position p " +
           "LEFT JOIN FETCH p.department " +
           "LEFT JOIN FETCH p.parentPosition " +
           "WHERE p.id = :id AND p.isDeleted = false")
    Optional<Position> findByIdWithRelations(@Param("id") Integer id);
}