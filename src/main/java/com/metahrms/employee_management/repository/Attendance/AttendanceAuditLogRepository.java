package com.metahrms.employee_management.repository.Attendance;

import com.metahrms.employee_management.entity.Attendance.AttendanceAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceAuditLogRepository extends JpaRepository<AttendanceAuditLog, Integer> {

    /**
     * Lấy audit log của 1 attendance record
     * Sắp xếp mới nhất trước
     */
    List<AttendanceAuditLog> findByAttendanceIdOrderByCreatedAtDesc(Integer attendanceId);
}