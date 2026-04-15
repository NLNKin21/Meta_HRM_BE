package com.metahrms.employee_management.repository.Attendance;

import com.metahrms.employee_management.entity.Attendance.Shift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {

    /**
     * Kiểm tra code đã tồn tại
     */
    boolean existsByCodeAndIsDeletedFalse(String code);

    /**
     * Kiểm tra code đã tồn tại (trừ chính nó)
     */
    boolean existsByCodeAndIdNotAndIsDeletedFalse(String code, Integer id);

    /**
     * Tìm theo ID (chưa xoá)
     */
    Optional<Shift> findByIdAndIsDeletedFalse(Integer id);

    /**
     * Tìm theo code
     */
    Optional<Shift> findByCodeAndIsDeletedFalse(String code);

    /**
     * Danh sách có phân trang + filter
     */
    @Query("SELECT s FROM Shift s WHERE s.isDeleted = false " +
           "AND (:isActive IS NULL OR s.isActive = :isActive) " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "     LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "     LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Shift> findAllWithFilters(
        @Param("isActive") Boolean isActive,
        @Param("keyword") String keyword,
        Pageable pageable
    );

    /**
     * Đếm employee đang dùng shift này
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.shift.id = :shiftId AND e.isDeleted = false")
    long countEmployeesByShiftId(@Param("shiftId") Integer shiftId);
}