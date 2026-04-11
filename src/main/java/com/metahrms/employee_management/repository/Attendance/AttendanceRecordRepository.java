package com.metahrms.employee_management.repository.Attendance;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Attendance.AttendanceRecord;
import com.metahrms.employee_management.enums.Attendance.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho AttendanceRecord entity
 */
@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Integer> {
    
    /**
     * Tìm attendance record của employee trong một ngày cụ thể
     */
    Optional<AttendanceRecord> findByEmployeeIdAndDate(Integer employeeId, LocalDate date);
    
    /**
     * Tìm attendance records trong khoảng thời gian
     */
    List<AttendanceRecord> findByEmployeeIdAndDateBetween(
        Integer employeeId,
        LocalDate startDate,
        LocalDate endDate
    );
    
    /**
     * Tìm tất cả records của employee (sắp xếp theo date giảm dần)
     */
    List<AttendanceRecord> findByEmployeeIdOrderByDateDesc(Integer employeeId);
    
    /**
     * Tìm records theo status
     */
    List<AttendanceRecord> findByEmployeeIdAndStatus(Integer employeeId, AttendanceStatus status);
    
    /**
     * Tìm records theo status và date range
     */
    List<AttendanceRecord> findByEmployeeIdAndStatusAndDateBetween(
        Integer employeeId,
        AttendanceStatus status,
        LocalDate startDate,
        LocalDate endDate
    );
    
    /**
     * Tìm records chưa verified
     */
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.employee.id = :employeeId AND ar.isVerified = false")
    List<AttendanceRecord> findUnverifiedByEmployeeId(@Param("employeeId") Integer employeeId);
    
    /**
     * Tìm records chưa approved
     */
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.employee.id = :employeeId AND ar.isApproved IS NULL")
    List<AttendanceRecord> findPendingApprovalByEmployeeId(@Param("employeeId") Integer employeeId);
    
    /**
     * Đếm số ngày đi làm trong tháng
     */
    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar " +
           "WHERE ar.employee.id = :employeeId " +
           "AND ar.status IN ('PRESENT', 'LATE', 'EARLY_LEAVE') " +
           "AND YEAR(ar.date) = :year " +
           "AND MONTH(ar.date) = :month")
    long countWorkingDaysByEmployeeAndMonth(
        @Param("employeeId") Integer employeeId,
        @Param("year") int year,
        @Param("month") int month
    );
    
    /**
     * Đếm số lần đi muộn trong tháng
     */
    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar " +
           "WHERE ar.employee.id = :employeeId " +
           "AND ar.status = 'LATE' " +
           "AND YEAR(ar.date) = :year " +
           "AND MONTH(ar.date) = :month")
    long countLateDaysByEmployeeAndMonth(
        @Param("employeeId") Integer employeeId,
        @Param("year") int year,
        @Param("month") int month
    );
    
    /**
     * Tính tổng work hours trong tháng
     */
    @Query("SELECT COALESCE(SUM(ar.workHours), 0) FROM AttendanceRecord ar " +
           "WHERE ar.employee.id = :employeeId " +
           "AND YEAR(ar.date) = :year " +
           "AND MONTH(ar.date) = :month")
    Double sumWorkHoursByEmployeeAndMonth(
        @Param("employeeId") Integer employeeId,
        @Param("year") int year,
        @Param("month") int month
    );
    
    /**
     * Tính tổng overtime hours trong tháng
     */
    @Query("SELECT COALESCE(SUM(ar.overtimeHours), 0) FROM AttendanceRecord ar " +
           "WHERE ar.employee.id = :employeeId " +
           "AND YEAR(ar.date) = :year " +
           "AND MONTH(ar.date) = :month")
    Double sumOvertimeHoursByEmployeeAndMonth(
        @Param("employeeId") Integer employeeId,
        @Param("year") int year,
        @Param("month") int month
    );
    
    /**
     * Tìm records có check-in nhưng chưa check-out
     */
    @Query("SELECT ar FROM AttendanceRecord ar " +
           "WHERE ar.checkInTime IS NOT NULL " +
           "AND ar.checkOutTime IS NULL " +
           "AND ar.date < :beforeDate")
    List<AttendanceRecord> findIncompleteRecords(@Param("beforeDate") LocalDate beforeDate);
    
    /**
     * Tìm records theo shift
     */
    List<AttendanceRecord> findByShiftId(Integer shiftId);
    
    /**
     * Tìm records theo location
     */
    List<AttendanceRecord> findByCheckInLocationId(Integer locationId);
    
    /**
     * Tìm tất cả records trong một ngày (cho admin)
     */
    List<AttendanceRecord> findByDate(LocalDate date);
    
    /**
     * Tìm records theo date range (cho admin)
     */
    List<AttendanceRecord> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Tìm records có anomalies
     */
    @Query("SELECT DISTINCT ar FROM AttendanceRecord ar " +
           "JOIN AttendanceAnomaly aa ON aa.attendance.id = ar.id " +
           "WHERE ar.employee.id = :employeeId " +
           "AND aa.resolved = false")
    List<AttendanceRecord> findRecordsWithUnresolvedAnomalies(@Param("employeeId") Integer employeeId);
    
    /**
     * Kiểm tra employee đã check-in hôm nay chưa
     */
    @Query("SELECT CASE WHEN COUNT(ar) > 0 THEN true ELSE false END " +
           "FROM AttendanceRecord ar " +
           "WHERE ar.employee.id = :employeeId " +
           "AND ar.date = :date " +
           "AND ar.checkInTime IS NOT NULL")
    boolean hasCheckedInToday(@Param("employeeId") Integer employeeId, @Param("date") LocalDate date);
    
    /**
     * Kiểm tra employee đã check-out hôm nay chưa
     */
    @Query("SELECT CASE WHEN COUNT(ar) > 0 THEN true ELSE false END " +
           "FROM AttendanceRecord ar " +
           "WHERE ar.employee.id = :employeeId " +
           "AND ar.date = :date " +
           "AND ar.checkOutTime IS NOT NULL")
    boolean hasCheckedOutToday(@Param("employeeId") Integer employeeId, @Param("date") LocalDate date);
    
    /**
     * Lấy attendance summary theo department
     */
    @Query("SELECT ar FROM AttendanceRecord ar " +
           "WHERE ar.employee.deptId = :departmentId " +
           "AND ar.date BETWEEN :startDate AND :endDate")
    List<AttendanceRecord> findByDepartmentAndDateBetween(
        @Param("departmentId") Integer departmentId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Tìm records với low face match score (cần review)
     */
    @Query("SELECT ar FROM AttendanceRecord ar " +
           "WHERE (ar.checkInFaceMatchScore < :threshold OR ar.checkOutFaceMatchScore < :threshold) " +
           "AND ar.date BETWEEN :startDate AND :endDate")
    List<AttendanceRecord> findLowConfidenceRecords(
        @Param("threshold") Double threshold,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}