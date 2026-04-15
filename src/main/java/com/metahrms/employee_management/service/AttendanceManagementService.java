package com.metahrms.employee_management.service;



import java.time.LocalDate;
import java.util.List;

import com.metahrms.employee_management.dto.request.Attendance.management.ApproveAttendanceRequest;
import com.metahrms.employee_management.dto.request.Attendance.management.EditAttendanceRequest;
import com.metahrms.employee_management.dto.request.Attendance.management.RejectAttendanceRequest;
import com.metahrms.employee_management.dto.response.Attendance.management.AuditLogDTO;
import com.metahrms.employee_management.dto.response.Attendance.management.DepartmentDailyAttendanceDTO;
import com.metahrms.employee_management.dto.response.Attendance.management.DepartmentMonthlyReportDTO;
import com.metahrms.employee_management.dto.response.Attendance.management.DepartmentSummaryDTO;

public interface AttendanceManagementService {

    /**
     * Lấy danh sách attendance theo ngày của 1 phòng ban
     * Bao gồm cả nhân viên chưa có record (absent)
     */
    DepartmentDailyAttendanceDTO getDepartmentDailyAttendance(
        Integer deptId,
        LocalDate date,
        Integer requestUserId
    );

    /**
     * Tóm tắt attendance của phòng ban trong ngày
     */
    DepartmentSummaryDTO getDepartmentSummary(
        Integer deptId,
        LocalDate date,
        Integer requestUserId
    );

    /**
     * Báo cáo attendance tháng của phòng ban
     */
    DepartmentMonthlyReportDTO getDepartmentMonthlyReport(
        Integer deptId,
        int year,
        int month,
        Integer requestUserId
    );

    /**
     * Duyệt attendance record
     */
    void approveAttendance(
        Integer attendanceId,
        ApproveAttendanceRequest request,
        Integer requestUserId
    );

    /**
     * Từ chối attendance record
     */
    void rejectAttendance(
        Integer attendanceId,
        RejectAttendanceRequest request,
        Integer requestUserId
    );

    /**
     * Chỉnh sửa attendance record (kèm audit log)
     */
    void editAttendance(
        Integer attendanceId,
        EditAttendanceRequest request,
        Integer requestUserId
    );

    /**
     * Lấy audit log của 1 attendance record
     */
    List<AuditLogDTO> getAuditLogs(Integer attendanceId, Integer requestUserId);

    /**
     * Kiểm tra user có quyền quản lý phòng ban không
     * ADMIN/HR → tất cả
     * MANAGER (HEAD) → chỉ phòng mình
     */
    void validateDepartmentAccess(Integer requestUserId, Integer deptId);
}