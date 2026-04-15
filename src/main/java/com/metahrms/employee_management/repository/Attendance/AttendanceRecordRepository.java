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

    // tim ca lam viec cua myEmployee
    @Query("""
       SELECT ar FROM AttendanceRecord ar
       LEFT JOIN FETCH ar.shift
       WHERE ar.employee.id = :employeeId
       AND ar.date BETWEEN :startDate AND :endDate
       """)
       List<AttendanceRecord> findByEmployeeIdAndDateBetweenWithShift(
              @Param("employeeId") Integer employeeId,
              @Param("startDate") LocalDate startDate,
              @Param("endDate") LocalDate endDate
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
     * Đếm số records có check-in trong ngày (toàn công ty)
     */
    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar " +
           "WHERE ar.date = :date " +
           "AND ar.checkInTime IS NOT NULL")
    long countCheckedInByDate(@Param("date") LocalDate date);

    /**
     * Đếm số records có check-out trong ngày
     */
    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar " +
           "WHERE ar.date = :date " +
           "AND ar.checkOutTime IS NOT NULL")
    long countCheckedOutByDate(@Param("date") LocalDate date);

    /**
     * Lấy tất cả records trong ngày (toàn công ty)
     */
    @Query("SELECT ar FROM AttendanceRecord ar " +
           "WHERE ar.date = :date " +
           "ORDER BY ar.checkInTime DESC")
    List<AttendanceRecord> findAllByDate(@Param("date") LocalDate date);

    /**
     * Đếm pending approvals (isApproved = null, đã check-in)
     */
    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar " +
           "WHERE ar.isApproved IS NULL " +
           "AND ar.checkInTime IS NOT NULL " +
           "AND ar.date >= :fromDate")
    long countPendingApprovals(@Param("fromDate") LocalDate fromDate);

    /**
     * Lấy records 7 ngày gần nhất (cho weekly trend)
     */
    @Query("SELECT ar FROM AttendanceRecord ar " +
           "WHERE ar.date BETWEEN :startDate AND :endDate " +
           "ORDER BY ar.date ASC")
    List<AttendanceRecord> findByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Lấy recent check-in/check-out activities
     * Dùng cho dashboard "recent activities"
     */
    @Query("SELECT ar FROM AttendanceRecord ar " +
           "WHERE ar.date = :date " +
           "AND ar.checkInTime IS NOT NULL " +
           "ORDER BY ar.checkInTime DESC")
    List<AttendanceRecord> findRecentActivities(
        @Param("date") LocalDate date,
        org.springframework.data.domain.Pageable pageable
    );

    /**
     * Tổng work hours toàn công ty trong ngày
     */
    @Query("SELECT COALESCE(SUM(ar.workHours), 0) FROM AttendanceRecord ar " +
           "WHERE ar.date = :date")
    Double sumWorkHoursByDate(@Param("date") LocalDate date);

    /**
     * Tổng overtime hours toàn công ty trong ngày
     */
    @Query("SELECT COALESCE(SUM(ar.overtimeHours), 0) FROM AttendanceRecord ar " +
           "WHERE ar.date = :date")
    Double sumOvertimeHoursByDate(@Param("date") LocalDate date);

    /**
     * Tổng work hours toàn công ty trong khoảng thời gian
     */
    @Query("SELECT COALESCE(SUM(ar.workHours), 0) FROM AttendanceRecord ar " +
           "WHERE ar.date BETWEEN :startDate AND :endDate " +
           "AND ar.employeeId IN :employeeIds")
    Double sumWorkHoursByEmployeeIdsAndDateRange(
        @Param("employeeIds") List<Integer> employeeIds,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Thống kê theo status trong khoảng thời gian cho nhiều employees
     */
    @Query("SELECT ar.status, COUNT(ar) FROM AttendanceRecord ar " +
           "WHERE ar.employeeId IN :employeeIds " +
           "AND ar.date BETWEEN :startDate AND :endDate " +
           "GROUP BY ar.status")
    List<Object[]> countByStatusForEmployees(
        @Param("employeeIds") List<Integer> employeeIds,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Top employees by present days trong tháng
     */
    @Query("SELECT ar.employeeId, COUNT(ar) as presentCount " +
           "FROM AttendanceRecord ar " +
           "WHERE YEAR(ar.date) = :year AND MONTH(ar.date) = :month " +
           "AND ar.status IN ('PRESENT', 'LATE', 'EARLY_LEAVE') " +
           "GROUP BY ar.employeeId " +
           "ORDER BY presentCount DESC")
    List<Object[]> findTopByPresentDays(
        @Param("year") int year,
        @Param("month") int month,
        org.springframework.data.domain.Pageable pageable
    );

    /**
     * Top employees by late days trong tháng
     */
    @Query("SELECT ar.employeeId, COUNT(ar) as lateCount " +
           "FROM AttendanceRecord ar " +
           "WHERE YEAR(ar.date) = :year AND MONTH(ar.date) = :month " +
           "AND ar.status = 'LATE' " +
           "GROUP BY ar.employeeId " +
           "ORDER BY lateCount DESC")
    List<Object[]> findTopByLateDays(
        @Param("year") int year,
        @Param("month") int month,
        org.springframework.data.domain.Pageable pageable
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

    /**
     * Lấy records theo tháng, sắp xếp theo ngày
     * Dùng trong getMyMonthlyCalendar()
     */
    @Query("SELECT ar FROM AttendanceRecord ar " +
           "WHERE ar.employeeId = :employeeId " +
           "AND YEAR(ar.date) = :year " +
           "AND MONTH(ar.date) = :month " +
           "ORDER BY ar.date ASC")
    List<AttendanceRecord> findByEmployeeIdAndYearMonth(
        @Param("employeeId") Integer employeeId,
        @Param("year") int year,
        @Param("month") int month
    );

    /**
     * Đếm số ngày theo status trong tháng
     * Dùng trong getMySummary()
     */
    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar " +
           "WHERE ar.employeeId = :employeeId " +
           "AND YEAR(ar.date) = :year " +
           "AND MONTH(ar.date) = :month " +
           "AND ar.status = :status")
    long countByEmployeeIdAndYearMonthAndStatus(
        @Param("employeeId") Integer employeeId,
        @Param("year") int year,
        @Param("month") int month,
        @Param("status") AttendanceStatus status
    );

    /**
     * Tính tổng work hours trong tháng
     * Dùng trong getMySummary()
     */
    @Query("SELECT COALESCE(SUM(ar.workHours), 0) FROM AttendanceRecord ar " +
           "WHERE ar.employeeId = :employeeId " +
           "AND YEAR(ar.date) = :year " +
           "AND MONTH(ar.date) = :month")
    Double sumWorkHoursByEmployeeIdAndYearMonth(
        @Param("employeeId") Integer employeeId,
        @Param("year") int year,
        @Param("month") int month
    );

    /**
     * Tính tổng overtime hours trong tháng
     */
    @Query("SELECT COALESCE(SUM(ar.overtimeHours), 0) FROM AttendanceRecord ar " +
           "WHERE ar.employeeId = :employeeId " +
           "AND YEAR(ar.date) = :year " +
           "AND MONTH(ar.date) = :month")
    Double sumOvertimeHoursByEmployeeIdAndYearMonth(
        @Param("employeeId") Integer employeeId,
        @Param("year") int year,
        @Param("month") int month
    );

    /**
     * Tính tổng late minutes trong tháng
     */
    @Query("SELECT COALESCE(SUM(ar.lateMinutes), 0) FROM AttendanceRecord ar " +
           "WHERE ar.employeeId = :employeeId " +
           "AND YEAR(ar.date) = :year " +
           "AND MONTH(ar.date) = :month")
    Long sumLateMinutesByEmployeeIdAndYearMonth(
        @Param("employeeId") Integer employeeId,
        @Param("year") int year,
        @Param("month") int month
    );

    /**
     * Tính tổng early leave minutes trong tháng
     */
    @Query("SELECT COALESCE(SUM(ar.earlyLeaveMinutes), 0) FROM AttendanceRecord ar " +
           "WHERE ar.employeeId = :employeeId " +
           "AND YEAR(ar.date) = :year " +
           "AND MONTH(ar.date) = :month")
    Long sumEarlyLeaveMinutesByEmployeeIdAndYearMonth(
        @Param("employeeId") Integer employeeId,
        @Param("year") int year,
        @Param("month") int month
    );

    /**
     * Lấy tất cả records của nhiều employees trong 1 ngày
     * Dùng trong getDepartmentDailyAttendance()
     */
    @Query("SELECT ar FROM AttendanceRecord ar " +
           "WHERE ar.employeeId IN :employeeIds " +
           "AND ar.date = :date")
    List<AttendanceRecord> findByEmployeeIdsAndDate(
        @Param("employeeIds") List<Integer> employeeIds,
        @Param("date") LocalDate date
    );

    /**
     * Lấy records của nhiều employees trong khoảng thời gian
     * Dùng trong getDepartmentMonthlyReport()
     */
    @Query("SELECT ar FROM AttendanceRecord ar " +
           "WHERE ar.employeeId IN :employeeIds " +
           "AND ar.date BETWEEN :startDate AND :endDate " +
           "ORDER BY ar.employeeId ASC, ar.date ASC")
    List<AttendanceRecord> findByEmployeeIdsAndDateBetween(
        @Param("employeeIds") List<Integer> employeeIds,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Tính tổng work hours của nhiều employees trong tháng
     * Dùng trong department summary
     */
    @Query("SELECT COALESCE(SUM(ar.workHours), 0) FROM AttendanceRecord ar " +
           "WHERE ar.employeeId IN :employeeIds " +
           "AND YEAR(ar.date) = :year " +
           "AND MONTH(ar.date) = :month")
    Double sumWorkHoursByEmployeeIdsAndYearMonth(
        @Param("employeeIds") List<Integer> employeeIds,
        @Param("year") int year,
        @Param("month") int month
    );

    /**
     * Tính tổng overtime hours của nhiều employees trong tháng
     */
    @Query("SELECT COALESCE(SUM(ar.overtimeHours), 0) FROM AttendanceRecord ar " +
           "WHERE ar.employeeId IN :employeeIds " +
           "AND YEAR(ar.date) = :year " +
           "AND MONTH(ar.date) = :month")
    Double sumOvertimeHoursByEmployeeIdsAndYearMonth(
        @Param("employeeIds") List<Integer> employeeIds,
        @Param("year") int year,
        @Param("month") int month
    );
}