package com.metahrms.employee_management.repository.Attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Attendance.AttendanceAnomaly;
import com.metahrms.employee_management.enums.Attendance.AnomalySeverity;
import com.metahrms.employee_management.enums.Attendance.AnomalyType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository cho AttendanceAnomaly entity
 */
@Repository
public interface AttendanceAnomalyRepository extends JpaRepository<AttendanceAnomaly, Integer> {
    
    /**
     * Tìm tất cả anomalies của một attendance record
     */
    List<AttendanceAnomaly> findByAttendance_Id(Integer attendanceId);
    
    /**
     * Tìm anomalies chưa resolved của một attendance record
     */
    List<AttendanceAnomaly> findByAttendance_IdAndResolvedFalse(Integer attendanceId);
    
    /**
     * Tìm tất cả anomalies của employee
     */
    @Query("SELECT aa FROM AttendanceAnomaly aa " +
           "WHERE aa.attendance.employee.id = :employeeId " +
           "ORDER BY aa.createdAt DESC")
    List<AttendanceAnomaly> findByEmployeeId(@Param("employeeId") Integer employeeId);
    
    /**
     * Tìm anomalies chưa resolved của employee
     */
    @Query("SELECT aa FROM AttendanceAnomaly aa " +
           "WHERE aa.attendance.employee.id = :employeeId " +
           "AND aa.resolved = false " +
           "ORDER BY aa.createdAt DESC")
    List<AttendanceAnomaly> findUnresolvedByEmployeeId(@Param("employeeId") Integer employeeId);
    
    /**
     * Tìm anomalies theo type
     */
    List<AttendanceAnomaly> findByAnomalyType(AnomalyType anomalyType);
    
    /**
     * Tìm anomalies theo severity
     */
    List<AttendanceAnomaly> findBySeverity(AnomalySeverity severity);
    

    /**
     * Đếm anomalies chưa resolve
     */
    @Query("SELECT COUNT(a) FROM AttendanceAnomaly a WHERE a.resolved = false")
    long countUnresolved();

    /**
     * Đếm critical anomalies chưa resolve
     */
    @Query("SELECT COUNT(a) FROM AttendanceAnomaly a " +
           "WHERE a.resolved = false " +
           "AND a.severity = 'CRITICAL'")
    long countUnresolvedCritical();

    
    /**
     * Tìm anomalies theo type và severity
     */
    List<AttendanceAnomaly> findByAnomalyTypeAndSeverity(
        AnomalyType anomalyType,
        AnomalySeverity severity
    );
    
    /**
     * Tìm critical anomalies chưa resolved
     */
    @Query("SELECT aa FROM AttendanceAnomaly aa " +
           "WHERE aa.severity = 'CRITICAL' " +
           "AND aa.resolved = false " +
           "ORDER BY aa.createdAt DESC")
    List<AttendanceAnomaly> findUnresolvedCriticalAnomalies();
    
    /**
     * Tìm anomalies trong khoảng thời gian
     */
    @Query("SELECT aa FROM AttendanceAnomaly aa " +
           "WHERE aa.attendance.date BETWEEN :startDate AND :endDate " +
           "ORDER BY aa.createdAt DESC")
    List<AttendanceAnomaly> findByDateBetween(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Đếm anomalies theo type cho một employee
     */
    @Query("SELECT COUNT(aa) FROM AttendanceAnomaly aa " +
           "WHERE aa.attendance.employee.id = :employeeId " +
           "AND aa.anomalyType = :type")
    long countByEmployeeIdAndType(
        @Param("employeeId") Integer employeeId,
        @Param("type") AnomalyType type
    );
    
    /**
     * Đếm anomalies chưa resolved
     */
    long countByResolvedFalse();
    
    /**
     * Đếm anomalies chưa resolved của employee
     */
    @Query("SELECT COUNT(aa) FROM AttendanceAnomaly aa " +
           "WHERE aa.attendance.employee.id = :employeeId " +
           "AND aa.resolved = false")
    long countUnresolvedByEmployeeId(@Param("employeeId") Integer employeeId);
    
    /**
     * Resolve một anomaly
     */
    @Modifying
    @Query("UPDATE AttendanceAnomaly aa " +
           "SET aa.resolved = true, " +
           "aa.resolvedBy = :resolvedBy, " +
           "aa.resolvedAt = :resolvedAt, " +
           "aa.resolutionNote = :note " +
           "WHERE aa.id = :id")
    void resolveAnomaly(
        @Param("id") Integer id,
        @Param("resolvedBy") Integer resolvedBy,
        @Param("resolvedAt") LocalDateTime resolvedAt,
        @Param("note") String note
    );
    
    /**
     * Resolve tất cả anomalies của một attendance record
     */
    @Modifying
    @Query("UPDATE AttendanceAnomaly aa " +
           "SET aa.resolved = true, " +
           "aa.resolvedBy = :resolvedBy, " +
           "aa.resolvedAt = :resolvedAt, " +
           "aa.resolutionNote = :note " +
           "WHERE aa.attendance.id = :attendanceId " +
           "AND aa.resolved = false")
    void resolveAllByAttendanceId(
        @Param("attendanceId") Integer attendanceId,
        @Param("resolvedBy") Integer resolvedBy,
        @Param("resolvedAt") LocalDateTime resolvedAt,
        @Param("note") String note
    );
    
    /**
     * Tìm anomalies theo department
     */
    @Query("SELECT aa FROM AttendanceAnomaly aa " +
           "WHERE aa.attendance.employee.deptId = :departmentId " +
           "AND aa.resolved = false")
    List<AttendanceAnomaly> findUnresolvedByDepartmentId(@Param("departmentId") Integer departmentId);
    
    /**
     * Thống kê anomalies theo type (cho dashboard)
     */
    @Query("SELECT aa.anomalyType, COUNT(aa) FROM AttendanceAnomaly aa " +
           "WHERE aa.attendance.date BETWEEN :startDate AND :endDate " +
           "GROUP BY aa.anomalyType")
    List<Object[]> countAnomaliesByType(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Thống kê anomalies theo severity
     */
    @Query("SELECT aa.severity, COUNT(aa) FROM AttendanceAnomaly aa " +
           "WHERE aa.attendance.date BETWEEN :startDate AND :endDate " +
           "GROUP BY aa.severity")
    List<Object[]> countAnomaliesBySeverity(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Tìm FACE_MISMATCH anomalies (cần review khẩn cấp)
     */
    @Query("SELECT aa FROM AttendanceAnomaly aa " +
           "WHERE aa.anomalyType = 'FACE_MISMATCH' " +
           "AND aa.resolved = false " +
           "ORDER BY aa.createdAt DESC")
    List<AttendanceAnomaly> findUnresolvedFaceMismatchAnomalies();
    
    /**
     * Xóa anomalies cũ (optional - cho cleanup job)
     */
    @Modifying
    @Query("DELETE FROM AttendanceAnomaly aa " +
           "WHERE aa.resolved = true " +
           "AND aa.resolvedAt < :beforeDate")
    void deleteResolvedOlderThan(@Param("beforeDate") LocalDateTime beforeDate);
}