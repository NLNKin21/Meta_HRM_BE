package com.metahrms.employee_management.repository.Attendance;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Attendance.Shift;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho Shift entity
 */
@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    
    /**
     * Tìm shift theo code (unique)
     */
    Optional<Shift> findByCode(String code);
    
    /**
     * Tìm tất cả shifts đang active
     */
    List<Shift> findByIsActiveTrue();
    
    /**
     * Tìm shift theo name (có thể trùng)
     */
    List<Shift> findByName(String name);
    
    /**
     * Kiểm tra code đã tồn tại chưa
     */
    boolean existsByCode(String code);
    
    /**
     * Kiểm tra code đã tồn tại (exclude id hiện tại - dùng khi update)
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM Shift s WHERE s.code = :code AND s.id != :id")
    boolean existsByCodeAndIdNot(@Param("code") String code, @Param("id") Integer id);
    
    /**
     * Tìm shifts theo work days (ví dụ: tìm các ca làm thứ 2)
     * JSON_CONTAINS không support ở mọi DB, dùng LIKE thay thế
     */
    @Query("SELECT s FROM Shift s WHERE s.workDays LIKE %:dayOfWeek%")
    List<Shift> findByWorkDay(@Param("dayOfWeek") String dayOfWeek);
    
    /**
     * Tìm shift theo time range
     */
    @Query("SELECT s FROM Shift s " +
           "WHERE s.startTime <= :time " +
           "AND s.endTime >= :time " +
           "AND s.isActive = true")
    List<Shift> findByTimeInRange(@Param("time") LocalTime time);
    
    /**
     * Tìm shifts có overtime (end time - start time > 8 hours)
     */
    @Query("SELECT s FROM Shift s " +
           "WHERE TIMESTAMPDIFF(HOUR, s.startTime, s.endTime) > 8 " +
           "AND s.isActive = true")
    List<Shift> findOvertimeShifts();
    
    /**
     * Tìm shifts theo late threshold
     */
    List<Shift> findByLateThreshold(Integer lateThreshold);
    
    /**
     * Đếm số employees được assign vào shift
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.shift.id = :shiftId")
    long countEmployeesByShiftId(@Param("shiftId") Integer shiftId);
    
    /**
     * Tìm shifts chưa có employee nào
     */
    @Query("SELECT s FROM Shift s " +
           "WHERE s.id NOT IN (SELECT DISTINCT e.shift.id FROM Employee e WHERE e.shift IS NOT NULL) " +
           "AND s.isActive = true")
    List<Shift> findUnassignedShifts();
    
    /**
     * Tìm shifts theo color (cho UI grouping)
     */
    List<Shift> findByColor(String color);
    
    /**
     * Search shifts by name or code
     */
    @Query("SELECT s FROM Shift s " +
           "WHERE (LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND s.isActive = true")
    List<Shift> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * Tìm shifts có break duration >= một giá trị
     */
    @Query("SELECT s FROM Shift s WHERE s.breakDuration >= :minBreakDuration")
    List<Shift> findByMinBreakDuration(@Param("minBreakDuration") Integer minBreakDuration);
}