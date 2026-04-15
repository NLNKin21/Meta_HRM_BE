package com.metahrms.employee_management.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.metahrms.employee_management.entity.Attendance.AttendanceAuditLog;
import com.metahrms.employee_management.entity.Attendance.AttendanceRecord;
import com.metahrms.employee_management.entity.Attendance.Shift;
import com.metahrms.employee_management.dto.request.Attendance.management.ApproveAttendanceRequest;
import com.metahrms.employee_management.dto.request.Attendance.management.EditAttendanceRequest;
import com.metahrms.employee_management.dto.request.Attendance.management.RejectAttendanceRequest;
import com.metahrms.employee_management.dto.response.Attendance.DayRecordDTO;
import com.metahrms.employee_management.dto.response.Attendance.management.AuditLogDTO;
import com.metahrms.employee_management.dto.response.Attendance.management.DepartmentDailyAttendanceDTO;
import com.metahrms.employee_management.dto.response.Attendance.management.DepartmentMonthlyReportDTO;
import com.metahrms.employee_management.dto.response.Attendance.management.DepartmentSummaryDTO;
import com.metahrms.employee_management.dto.response.Attendance.management.EmployeeAttendanceDTO;
import com.metahrms.employee_management.dto.response.Attendance.management.MonthlyEmployeeReportDTO;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.enums.Attendance.AttendanceStatus;
import com.metahrms.employee_management.enums.RoleInDepartment;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.Attendance.AttendanceAuditLogRepository;
import com.metahrms.employee_management.repository.Attendance.AttendanceRecordRepository;
import com.metahrms.employee_management.repository.Attendance.ShiftRepository;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.service.AttendanceManagementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceManagementServiceImpl implements AttendanceManagementService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceAuditLogRepository auditLogRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ShiftRepository shiftRepository;
    private final ObjectMapper objectMapper;

    // ============================================
    // GET DEPARTMENT DAILY ATTENDANCE
    // ============================================

    @Override
    public DepartmentDailyAttendanceDTO getDepartmentDailyAttendance(
            Integer deptId,
            LocalDate date,
            Integer requestUserId) {

        log.info("[MGMT] Getting daily attendance: deptId={}, date={}, userId={}",
                deptId, date, requestUserId);

        // 1. Kiểm tra quyền
        validateDepartmentAccess(requestUserId, deptId);

        // 2. Lấy thông tin phòng ban
        Department dept = getDepartmentById(deptId);

        // 3. Lấy danh sách nhân viên trong phòng
        List<Employee> employees = employeeRepository.findByDeptIdWithDetails(deptId);

        if (employees.isEmpty()) {
            return DepartmentDailyAttendanceDTO.builder()
                    .deptId(deptId)
                    .deptName(dept.getDeptName())
                    .date(date)
                    .totalEmployees(0)
                    .presentCount(0)
                    .absentCount(0)
                    .lateCount(0)
                    .earlyLeaveCount(0)
                    .notCheckedCount(0)
                    .leaveCount(0)
                    .employees(Collections.emptyList())
                    .build();
        }

        // 4. Lấy employee IDs
        List<Integer> employeeIds = employees.stream()
                .map(Employee::getId)
                .collect(Collectors.toList());

        // 5. Lấy tất cả attendance records của ngày đó (1 query)
        List<AttendanceRecord> records = attendanceRecordRepository
                .findByEmployeeIdsAndDate(employeeIds, date);

        // 6. Map employeeId → record để lookup O(1)
        Map<Integer, AttendanceRecord> recordMap = records.stream()
                .collect(Collectors.toMap(AttendanceRecord::getEmployeeId, Function.identity()));

        // 7. Build DTO cho từng nhân viên + đếm summary
        int presentCount = 0, absentCount = 0, lateCount = 0;
        int earlyLeaveCount = 0, notCheckedCount = 0, leaveCount = 0;

        List<EmployeeAttendanceDTO> employeeDTOs = new ArrayList<>();

        for (Employee emp : employees) {
            AttendanceRecord record = recordMap.get(emp.getId());
            Shift shift = getShiftForEmployee(emp);

            EmployeeAttendanceDTO dto = buildEmployeeAttendanceDTO(emp, record, shift, dept, date);
            employeeDTOs.add(dto);

            // Đếm status
            AttendanceStatus status = dto.getStatus();
            if (status == null) status = AttendanceStatus.ABSENT;

            switch (status) {
                case PRESENT    -> presentCount++;
                case LATE       -> { presentCount++; lateCount++; }
                case EARLY_LEAVE -> { presentCount++; earlyLeaveCount++; }
                case LEAVE      -> leaveCount++;
                case NOT_CHECKED -> notCheckedCount++;
                case ABSENT     -> absentCount++;
            }
        }

        return DepartmentDailyAttendanceDTO.builder()
                .deptId(deptId)
                .deptName(dept.getDeptName())
                .date(date)
                .totalEmployees(employees.size())
                .presentCount(presentCount)
                .absentCount(absentCount)
                .lateCount(lateCount)
                .earlyLeaveCount(earlyLeaveCount)
                .notCheckedCount(notCheckedCount)
                .leaveCount(leaveCount)
                .employees(employeeDTOs)
                .build();
    }

    // ============================================
    // GET DEPARTMENT SUMMARY
    // ============================================

    @Override
    public DepartmentSummaryDTO getDepartmentSummary(
            Integer deptId,
            LocalDate date,
            Integer requestUserId) {

        log.info("[MGMT] Getting department summary: deptId={}, date={}", deptId, date);

        validateDepartmentAccess(requestUserId, deptId);

        Department dept = getDepartmentById(deptId);
        List<Employee> employees = employeeRepository.findByDeptIdWithDetails(deptId);
        int totalEmployees = employees.size();

        if (totalEmployees == 0) {
            return DepartmentSummaryDTO.builder()
                    .deptId(deptId)
                    .deptName(dept.getDeptName())
                    .date(date)
                    .totalEmployees(0)
                    .build();
        }

        List<Integer> employeeIds = employees.stream()
                .map(Employee::getId)
                .collect(Collectors.toList());

        List<AttendanceRecord> records = attendanceRecordRepository
                .findByEmployeeIdsAndDate(employeeIds, date);

        // Đếm
        int presentCount = 0, lateCount = 0, earlyLeaveCount = 0;
        int leaveCount = 0, notCheckedOutCount = 0;
        double totalWorkHours = 0, totalOvertimeHours = 0;

        Map<Integer, AttendanceRecord> recordMap = records.stream()
                .collect(Collectors.toMap(AttendanceRecord::getEmployeeId, Function.identity()));

        for (AttendanceRecord record : records) {
            AttendanceStatus status = record.getStatus();
            if (status == null) continue;

            switch (status) {
                case PRESENT    -> presentCount++;
                case LATE       -> { presentCount++; lateCount++; }
                case EARLY_LEAVE -> { presentCount++; earlyLeaveCount++; }
                case LEAVE      -> leaveCount++;
                default         -> {}
            }

            // Đã check-in nhưng chưa check-out
            if (record.getCheckInTime() != null && record.getCheckOutTime() == null) {
                notCheckedOutCount++;
            }

            if (record.getWorkHours() != null) {
                totalWorkHours += record.getWorkHours().doubleValue();
            }
            if (record.getOvertimeHours() != null) {
                totalOvertimeHours += record.getOvertimeHours().doubleValue();
            }
        }

        int notCheckedInCount = totalEmployees - records.size();
        int absentCount = Math.max(0, totalEmployees - presentCount - leaveCount
                - (int) records.stream()
                        .filter(r -> r.getStatus() == AttendanceStatus.NOT_CHECKED)
                        .count());

        double attendanceRate = totalEmployees > 0
                ? Math.round((double) presentCount / totalEmployees * 100 * 10) / 10.0
                : 0.0;

        double punctualityRate = presentCount > 0
                ? Math.round((double) (presentCount - lateCount) / presentCount * 100 * 10) / 10.0
                : 0.0;

        return DepartmentSummaryDTO.builder()
                .deptId(deptId)
                .deptName(dept.getDeptName())
                .date(date)
                .totalEmployees(totalEmployees)
                .presentCount(presentCount)
                .absentCount(absentCount)
                .lateCount(lateCount)
                .earlyLeaveCount(earlyLeaveCount)
                .leaveCount(leaveCount)
                .notCheckedInCount(notCheckedInCount)
                .notCheckedOutCount(notCheckedOutCount)
                .attendanceRate(attendanceRate)
                .punctualityRate(punctualityRate)
                .totalWorkHours(Math.round(totalWorkHours * 100.0) / 100.0)
                .totalOvertimeHours(Math.round(totalOvertimeHours * 100.0) / 100.0)
                .build();
    }

    // ============================================
    // GET DEPARTMENT MONTHLY REPORT
    // ============================================

    @Override
    public DepartmentMonthlyReportDTO getDepartmentMonthlyReport(
            Integer deptId,
            int year,
            int month,
            Integer requestUserId) {

        log.info("[MGMT] Getting monthly report: deptId={}, year={}, month={}", deptId, year, month);

        validateDepartmentAccess(requestUserId, deptId);
        validateYearMonth(year, month);

        Department dept = getDepartmentById(deptId);
        List<Employee> employees = employeeRepository.findByDeptIdWithDetails(deptId);

        if (employees.isEmpty()) {
            return DepartmentMonthlyReportDTO.builder()
                    .deptId(deptId)
                    .deptName(dept.getDeptName())
                    .year(year)
                    .month(month)
                    .totalEmployees(0)
                    .employees(Collections.emptyList())
                    .build();
        }

        List<Integer> employeeIds = employees.stream()
                .map(Employee::getId)
                .collect(Collectors.toList());

        // Lấy tất cả records trong tháng (1 query)
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<AttendanceRecord> allRecords = attendanceRecordRepository
                .findByEmployeeIdsAndDateBetween(employeeIds, startDate, endDate);

        // Group records by employeeId
        Map<Integer, List<AttendanceRecord>> recordsByEmployee = allRecords.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getEmployeeId));

        // Build report cho từng nhân viên
        List<MonthlyEmployeeReportDTO> employeeReports = new ArrayList<>();
        double totalAttendanceRate = 0;
        double totalWorkHours = 0;
        double totalOvertimeHours = 0;

        for (Employee emp : employees) {
            List<AttendanceRecord> empRecords = recordsByEmployee
                    .getOrDefault(emp.getId(), Collections.emptyList());

            Shift shift = getShiftForEmployee(emp);
            List<Integer> workDays = parseWorkDays(shift);

            MonthlyEmployeeReportDTO empReport = buildMonthlyEmployeeReport(
                    emp, empRecords, shift, workDays, year, month, yearMonth
            );

            employeeReports.add(empReport);
            totalAttendanceRate += empReport.getAttendanceRate() != null
                    ? empReport.getAttendanceRate() : 0;
            totalWorkHours += empReport.getTotalWorkHours() != null
                    ? empReport.getTotalWorkHours() : 0;
            totalOvertimeHours += empReport.getTotalOvertimeHours() != null
                    ? empReport.getTotalOvertimeHours() : 0;
        }

        double avgAttendanceRate = !employees.isEmpty()
                ? Math.round(totalAttendanceRate / employees.size() * 10) / 10.0
                : 0.0;

        String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        return DepartmentMonthlyReportDTO.builder()
                .deptId(deptId)
                .deptName(dept.getDeptName())
                .year(year)
                .month(month)
                .monthName(monthName)
                .totalEmployees(employees.size())
                .avgAttendanceRate(avgAttendanceRate)
                .totalWorkHours(Math.round(totalWorkHours * 100.0) / 100.0)
                .totalOvertimeHours(Math.round(totalOvertimeHours * 100.0) / 100.0)
                .employees(employeeReports)
                .build();
    }

    // ============================================
    // APPROVE ATTENDANCE
    // ============================================

    @Override
    @Transactional
    public void approveAttendance(
            Integer attendanceId,
            ApproveAttendanceRequest request,
            Integer requestUserId) {

        log.info("[MGMT] Approving attendance id={} by userId={}", attendanceId, requestUserId);

        AttendanceRecord record = getAttendanceById(attendanceId);

        // Kiểm tra quyền: phải có quyền với phòng của employee này
        Employee emp = getEmployeeById(record.getEmployeeId());
        validateDepartmentAccess(requestUserId, emp.getDeptId());

        // Không duyệt lại nếu đã duyệt
        if (Boolean.TRUE.equals(record.getIsApproved())) {
            throw new IllegalStateException("Attendance already approved");
        }

        // Snapshot trước khi thay đổi
        String oldValue = toJson(Map.of(
                "isApproved", record.getIsApproved(),
                "approvalNote", record.getApprovalNote() != null ? record.getApprovalNote() : ""
        ));

        // Update
        record.setIsApproved(true);
        record.setApprovedBy(requestUserId);
        record.setApprovedAt(LocalDateTime.now());
        record.setApprovalNote(request.getNote());

        attendanceRecordRepository.save(record);

        // Lưu audit log
        String newValue = toJson(Map.of(
                "isApproved", true,
                "approvalNote", request.getNote() != null ? request.getNote() : ""
        ));

        saveAuditLog(attendanceId, "APPROVE", oldValue, newValue, null, requestUserId);

        log.info("[MGMT] Approved attendance id={}", attendanceId);
    }

    // ============================================
    // REJECT ATTENDANCE
    // ============================================

    @Override
    @Transactional
    public void rejectAttendance(
            Integer attendanceId,
            RejectAttendanceRequest request,
            Integer requestUserId) {

        log.info("[MGMT] Rejecting attendance id={} by userId={}", attendanceId, requestUserId);

        AttendanceRecord record = getAttendanceById(attendanceId);

        Employee emp = getEmployeeById(record.getEmployeeId());
        validateDepartmentAccess(requestUserId, emp.getDeptId());

        String oldValue = toJson(Map.of(
                "isApproved", record.getIsApproved() != null ? record.getIsApproved() : false,
                "approvalNote", record.getApprovalNote() != null ? record.getApprovalNote() : ""
        ));

        // Reject = isApproved false + ghi note
        record.setIsApproved(false);
        record.setApprovedBy(requestUserId);
        record.setApprovedAt(LocalDateTime.now());
        record.setApprovalNote(request.getReason());

        attendanceRecordRepository.save(record);

        String newValue = toJson(Map.of(
                "isApproved", false,
                "reason", request.getReason()
        ));

        saveAuditLog(attendanceId, "REJECT", oldValue, newValue, request.getReason(), requestUserId);

        log.info("[MGMT] Rejected attendance id={}, reason={}", attendanceId, request.getReason());
    }

    // ============================================
    // EDIT ATTENDANCE
    // ============================================

    @Override
    @Transactional
    public void editAttendance(
            Integer attendanceId,
            EditAttendanceRequest request,
            Integer requestUserId) {

        log.info("[MGMT] Editing attendance id={} by userId={}", attendanceId, requestUserId);

        AttendanceRecord record = getAttendanceById(attendanceId);

        Employee emp = getEmployeeById(record.getEmployeeId());
        validateDepartmentAccess(requestUserId, emp.getDeptId());

        // Snapshot trước khi sửa
        String oldValue = toJson(Map.of(
                "checkInTime",  record.getCheckInTime() != null ? record.getCheckInTime().toString() : "",
                "checkOutTime", record.getCheckOutTime() != null ? record.getCheckOutTime().toString() : "",
                "status",       record.getStatus() != null ? record.getStatus().name() : "",
                "lateMinutes",  record.getLateMinutes() != null ? record.getLateMinutes() : 0,
                "earlyLeaveMinutes", record.getEarlyLeaveMinutes() != null ? record.getEarlyLeaveMinutes() : 0
        ));

        // Apply changes (chỉ update fields != null)
        if (request.getCheckInTime() != null) {
            record.setCheckInTime(request.getCheckInTime());
        }
        if (request.getCheckOutTime() != null) {
            record.setCheckOutTime(request.getCheckOutTime());
        }
        if (request.getStatus() != null) {
            record.setStatus(request.getStatus());
        }
        if (request.getLateMinutes() != null) {
            record.setLateMinutes(request.getLateMinutes());
        }
        if (request.getEarlyLeaveMinutes() != null) {
            record.setEarlyLeaveMinutes(request.getEarlyLeaveMinutes());
        }
        if (request.getNote() != null) {
            record.setNote(request.getNote());
        }

        // Tính lại work hours nếu thay đổi checkIn/Out time
        if (request.getCheckInTime() != null || request.getCheckOutTime() != null) {
            recalculateWorkHours(record);
        }

        // Reset approval (cần duyệt lại sau khi sửa)
        record.setIsApproved(null);
        record.setApprovedBy(null);
        record.setApprovedAt(null);

        attendanceRecordRepository.save(record);

        // Snapshot sau khi sửa
        String newValue = toJson(Map.of(
                "checkInTime",  record.getCheckInTime() != null ? record.getCheckInTime().toString() : "",
                "checkOutTime", record.getCheckOutTime() != null ? record.getCheckOutTime().toString() : "",
                "status",       record.getStatus() != null ? record.getStatus().name() : "",
                "lateMinutes",  record.getLateMinutes() != null ? record.getLateMinutes() : 0,
                "earlyLeaveMinutes", record.getEarlyLeaveMinutes() != null ? record.getEarlyLeaveMinutes() : 0
        ));

        saveAuditLog(attendanceId, "EDIT", oldValue, newValue, request.getReason(), requestUserId);

        log.info("[MGMT] Edited attendance id={}, reason={}", attendanceId, request.getReason());
    }

    // ============================================
    // GET AUDIT LOGS
    // ============================================

    @Override
    public List<AuditLogDTO> getAuditLogs(Integer attendanceId, Integer requestUserId) {
        log.info("[MGMT] Getting audit logs for attendanceId={}", attendanceId);

        // Kiểm tra attendance tồn tại + quyền
        AttendanceRecord record = getAttendanceById(attendanceId);
        Employee emp = getEmployeeById(record.getEmployeeId());
        validateDepartmentAccess(requestUserId, emp.getDeptId());

        List<AttendanceAuditLog> logs = auditLogRepository
                .findByAttendanceIdOrderByCreatedAtDesc(attendanceId);

        return logs.stream()
                .map(log -> AuditLogDTO.builder()
                        .id(log.getId())
                        .attendanceId(log.getAttendanceId())
                        .action(log.getAction())
                        .oldValue(log.getOldValue())
                        .newValue(log.getNewValue())
                        .reason(log.getReason())
                        .performedBy(log.getPerformedBy())
                        .performedByName(log.getPerformedByName())
                        .performedAt(log.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // ============================================
    // VALIDATE DEPARTMENT ACCESS
    // ============================================

    @Override
    public void validateDepartmentAccess(Integer requestUserId, Integer deptId) {
        String role = getCurrentUserRole(requestUserId);

        // ADMIN và HR có thể xem tất cả
        if ("ADMIN".equals(role) || "HR".equals(role)) {
            return;
        }

        // MANAGER chỉ được xem phòng mình quản lý
        if ("MANAGER".equals(role)) {
            // Tìm employee của user này
            Employee manager = employeeRepository
                    .findByUserIdAndIsDeletedFalse(requestUserId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Employee not found for userId: " + requestUserId
                    ));

            // Kiểm tra role trong phòng
            boolean isHeadOfDept = RoleInDepartment.HEAD.equals(manager.getRoleInDept())
                    && deptId.equals(manager.getDeptId());

            if (!isHeadOfDept) {
                throw new SecurityException(
                        "Access denied: You can only manage your own department"
                );
            }
            return;
        }

        // Role khác (EMPLOYEE...) không có quyền
        throw new SecurityException("Access denied: Insufficient permissions");
    }

    // ============================================
    // PRIVATE HELPERS
    // ============================================

    /**
     * Lấy role từ JWT thông qua Employee
     * SecurityUtils.getCurrentUserRole() trả về role string
     *
     * NOTE: Trong service không thể gọi SecurityUtils trực tiếp
     * → Nhận requestUserId từ Controller
     * → Lấy role từ request attribute thông qua helper
     */
    private String getCurrentUserRole(Integer requestUserId) {
        // SecurityUtils được gọi từ Controller và truyền vào
        // Ở đây ta dùng cách khác: lấy role từ SecurityContext trực tiếp
        try {
            org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();

            if (auth != null && auth.getAuthorities() != null) {
                return auth.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        // Spring Security thêm prefix ROLE_
                        .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                        .filter(a -> List.of("ADMIN", "HR", "MANAGER", "EMPLOYEE").contains(a))
                        .findFirst()
                        .orElse("EMPLOYEE");
            }
        } catch (Exception e) {
            log.error("[MGMT] Cannot get role from SecurityContext", e);
        }
        return "EMPLOYEE";
    }

    /**
     * Lấy shift của employee (load từ DB để tránh LazyInit)
     */
    private Shift getShiftForEmployee(Employee emp) {
        if (emp.getShift() == null) return null;
        return shiftRepository.findByIdAndIsDeletedFalse(emp.getShift().getId())
                .orElse(null);
    }

    /**
     * Parse workDays JSON → List<Integer>
     */
    private List<Integer> parseWorkDays(Shift shift) {
        if (shift == null || shift.getWorkDays() == null) {
            return List.of(1, 2, 3, 4, 5);
        }
        try {
            return objectMapper.readValue(shift.getWorkDays(), new TypeReference<List<Integer>>() {});
        } catch (Exception e) {
            log.error("[MGMT] Failed to parse workDays", e);
            return List.of(1, 2, 3, 4, 5);
        }
    }

    /**
     * Kiểm tra ngày có phải ngày làm việc không
     */
    private boolean isWorkDay(LocalDate date, List<Integer> workDays) {
        return workDays.contains(date.getDayOfWeek().getValue());
    }

    /**
     * Build EmployeeAttendanceDTO
     */
    private EmployeeAttendanceDTO buildEmployeeAttendanceDTO(
            Employee emp,
            AttendanceRecord record,
            Shift shift,
            Department dept,
            LocalDate date) {

            String positionName = null;
            try {
                if (emp.getPosition() != null) {
                    positionName = emp.getPosition().getPositionName(); // dùng getter cụ thể
                }
            } catch (Exception e) {
                log.warn("Cannot load position for employee {}", emp.getId());
                positionName = null;
            }

        EmployeeAttendanceDTO.EmployeeAttendanceDTOBuilder builder = EmployeeAttendanceDTO.builder()
                .employeeId(emp.getId())
                .fullName(emp.getFullName())
                .positionName(positionName)
                .deptId(dept.getId())
                .deptName(dept.getDeptName())
                .date(date);

        // Shift info
        if (shift != null) {
            builder.shiftId(shift.getId())
                   .shiftName(shift.getName())
                   .shiftStartTime(shift.getStartTime().toString())
                   .shiftEndTime(shift.getEndTime().toString());
        }

        // Nếu không có record → ABSENT (nếu ngày đã qua)
        if (record == null) {
            AttendanceStatus status = date.isBefore(LocalDate.now())
                    ? AttendanceStatus.ABSENT
                    : AttendanceStatus.NOT_CHECKED;
            builder.status(status);
            return builder.build();
        }

        // Có record
        builder.attendanceId(record.getId())
               .status(record.getStatus())
               .checkInTime(record.getCheckInTime())
               .checkOutTime(record.getCheckOutTime())
               .workHours(record.getWorkHours() != null ? record.getWorkHours().doubleValue() : null)
               .overtimeHours(record.getOvertimeHours() != null ? record.getOvertimeHours().doubleValue() : null)
               .lateMinutes(record.getLateMinutes())
               .earlyLeaveMinutes(record.getEarlyLeaveMinutes())
               .isVerified(record.getIsVerified())
               .isApproved(record.getIsApproved())
               .approvalNote(record.getApprovalNote())
               .checkInPhotoUrl(record.getCheckInPhotoUrl())
               .checkOutPhotoUrl(record.getCheckOutPhotoUrl())
               .checkInFaceMatchScore(record.getCheckInFaceMatchScore() != null
                       ? record.getCheckInFaceMatchScore().doubleValue() : null)
               .checkOutFaceMatchScore(record.getCheckOutFaceMatchScore() != null
                       ? record.getCheckOutFaceMatchScore().doubleValue() : null);

        return builder.build();
    }

    /**
     * Build MonthlyEmployeeReportDTO cho 1 nhân viên
     */
    private MonthlyEmployeeReportDTO buildMonthlyEmployeeReport(
            Employee emp,
            List<AttendanceRecord> records,
            Shift shift,
            List<Integer> workDays,
            int year,
            int month,
            YearMonth yearMonth) {

        // Map date → record
        Map<LocalDate, AttendanceRecord> recordMap = records.stream()
                .collect(Collectors.toMap(AttendanceRecord::getDate, Function.identity()));

        // Tính tổng ngày làm việc đã qua
        LocalDate today = LocalDate.now();
        int totalWorkDays = 0;
        for (int d = 1; d <= yearMonth.lengthOfMonth(); d++) {
            LocalDate date = LocalDate.of(year, month, d);
            if (isWorkDay(date, workDays) && !date.isAfter(today)) {
                totalWorkDays++;
            }
        }

        // Đếm status
        int presentDays = 0, lateDays = 0, earlyLeaveDays = 0, leaveDays = 0;
        double totalWorkHours = 0, totalOvertimeHours = 0;

        for (AttendanceRecord r : records) {
            if (r.getStatus() == null) continue;
            switch (r.getStatus()) {
                case PRESENT     -> presentDays++;
                case LATE        -> { presentDays++; lateDays++; }
                case EARLY_LEAVE -> { presentDays++; earlyLeaveDays++; }
                case LEAVE       -> leaveDays++;
                default          -> {}
            }
            if (r.getWorkHours() != null) totalWorkHours += r.getWorkHours().doubleValue();
            if (r.getOvertimeHours() != null) totalOvertimeHours += r.getOvertimeHours().doubleValue();
        }

        int absentDays = Math.max(0, totalWorkDays - presentDays - leaveDays);
        double attendanceRate = totalWorkDays > 0
                ? Math.round((double) presentDays / totalWorkDays * 100 * 10) / 10.0
                : 0.0;

        // Build danh sách ngày (calendar)
        List<DayRecordDTO> days = new ArrayList<>();
        for (int d = 1; d <= yearMonth.lengthOfMonth(); d++) {
            LocalDate date = LocalDate.of(year, month, d);
            AttendanceRecord record = recordMap.get(date);
            boolean isWorkDayFlag = isWorkDay(date, workDays);
            String dayOfWeek = date.getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            DayRecordDTO.DayRecordDTOBuilder dayBuilder = DayRecordDTO.builder()
                    .date(date)
                    .dayOfWeek(dayOfWeek)
                    .isWorkDay(isWorkDayFlag);

            if (shift != null) {
                dayBuilder.shiftName(shift.getName())
                          .shiftStartTime(shift.getStartTime().toString())
                          .shiftEndTime(shift.getEndTime().toString());
            }

            if (record != null) {
                dayBuilder.attendanceId(record.getId())
                          .status(record.getStatus())
                          .checkInTime(record.getCheckInTime())
                          .checkOutTime(record.getCheckOutTime())
                          .workHours(record.getWorkHours() != null ? record.getWorkHours().doubleValue() : null)
                          .overtimeHours(record.getOvertimeHours() != null ? record.getOvertimeHours().doubleValue() : null)
                          .lateMinutes(record.getLateMinutes())
                          .earlyLeaveMinutes(record.getEarlyLeaveMinutes())
                          .isVerified(record.getIsVerified())
                          .isApproved(record.getIsApproved());
            } else if (isWorkDayFlag && date.isBefore(today)) {
                dayBuilder.status(AttendanceStatus.ABSENT);
            }

            days.add(dayBuilder.build());
        }

        return MonthlyEmployeeReportDTO.builder()
                .employeeId(emp.getId())
                .fullName(emp.getFullName())
                .positionName(emp.getPosition() != null ? emp.getPosition().toString() : null)
                .shiftName(shift != null ? shift.getName() : null)
                .totalWorkDays(totalWorkDays)
                .presentDays(presentDays)
                .absentDays(absentDays)
                .lateDays(lateDays)
                .earlyLeaveDays(earlyLeaveDays)
                .leaveDays(leaveDays)
                .totalWorkHours(Math.round(totalWorkHours * 100.0) / 100.0)
                .totalOvertimeHours(Math.round(totalOvertimeHours * 100.0) / 100.0)
                .attendanceRate(attendanceRate)
                .days(days)
                .build();
    }

    /**
     * Tính lại work hours sau khi edit checkIn/Out time
     */
    private void recalculateWorkHours(AttendanceRecord record) {
        if (record.getCheckInTime() == null || record.getCheckOutTime() == null) {
            record.setWorkHours(BigDecimal.ZERO);
            record.setOvertimeHours(BigDecimal.ZERO);
            return;
        }

        long totalMinutes = java.time.Duration.between(
                record.getCheckInTime(), record.getCheckOutTime()
        ).toMinutes();

        long breakMinutes = 60; // default
        if (record.getShiftId() != null) {
            shiftRepository.findById(record.getShiftId())
                    .ifPresent(s -> {
                        // handled below
                    });
            Shift shift = shiftRepository.findById(record.getShiftId()).orElse(null);
            if (shift != null && shift.getBreakDuration() != null) {
                breakMinutes = shift.getBreakDuration();
            }
        }

        long workMinutes = Math.max(0, totalMinutes - breakMinutes);
        BigDecimal workHours = BigDecimal.valueOf(workMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        record.setWorkHours(workHours);
        record.setOvertimeHours(
                workHours.compareTo(BigDecimal.valueOf(8)) > 0
                        ? workHours.subtract(BigDecimal.valueOf(8))
                        : BigDecimal.ZERO
        );
    }

    /**
     * Lưu audit log
     */
    private void saveAuditLog(
            Integer attendanceId,
            String action,
            String oldValue,
            String newValue,
            String reason,
            Integer performedBy) {

        // Lấy tên người thực hiện
        String performedByName = employeeRepository
                .findByUserIdAndIsDeletedFalse(performedBy)
                .map(Employee::getFullName)
                .orElse("Unknown");

        AttendanceAuditLog auditLog = AttendanceAuditLog.builder()
                .attendanceId(attendanceId)
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .reason(reason)
                .performedBy(performedBy)
                .performedByName(performedByName)
                .build();

        auditLogRepository.save(auditLog);
    }

    /**
     * Convert Map → JSON string
     */
    private String toJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.error("[MGMT] Failed to serialize to JSON", e);
            return "{}";
        }
    }

    private Department getDepartmentById(Integer deptId) {
        return departmentRepository.findByIdAndIsDeletedFalse(deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + deptId));
    }

    private AttendanceRecord getAttendanceById(Integer id) {
        return attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found: " + id));
    }

    private Employee getEmployeeById(Integer id) {
        return employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    private void validateYearMonth(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year < 2000 || year > LocalDate.now().getYear() + 1) {
            throw new IllegalArgumentException("Year is out of valid range");
        }
    }
}