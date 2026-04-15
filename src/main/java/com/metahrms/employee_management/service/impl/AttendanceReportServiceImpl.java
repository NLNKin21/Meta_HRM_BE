package com.metahrms.employee_management.service.impl;

import com.metahrms.employee_management.dto.response.Attendance.management.DepartmentSummaryDTO;
import com.metahrms.employee_management.dto.response.report.CompanyDailyReportDTO;
import com.metahrms.employee_management.dto.response.report.CompanyMonthlyReportDTO;
import com.metahrms.employee_management.dto.response.report.DashboardDTO;
import com.metahrms.employee_management.dto.response.report.DepartmentComparisonDTO;
import com.metahrms.employee_management.entity.Attendance.AttendanceRecord;
import com.metahrms.employee_management.entity.Attendance.Shift;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.enums.Attendance.AttendanceStatus;
import com.metahrms.employee_management.repository.Attendance.AttendanceAnomalyRepository;
import com.metahrms.employee_management.repository.Attendance.AttendanceRecordRepository;
import com.metahrms.employee_management.repository.Attendance.ShiftRepository;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.service.AttendanceReportService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceReportServiceImpl implements AttendanceReportService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceAnomalyRepository anomalyRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ShiftRepository shiftRepository;

    // ============================================
    // DASHBOARD
    // ============================================

    @Override
    public DashboardDTO getDashboard() {
        log.info("[REPORT] Generating dashboard");

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // Tổng số nhân viên active
        List<Employee> allEmployees = employeeRepository.findAll().stream()
                .filter(e -> !Boolean.TRUE.equals(e.getIsDeleted()))
                .collect(Collectors.toList());
        int totalEmployees = allEmployees.size();

        // Records hôm nay
        List<AttendanceRecord> todayRecords = attendanceRecordRepository.findAllByDate(today);

        // Phân tích records
        int checkedInCount  = 0, checkedOutCount = 0, stillWorkingCount = 0;
        int presentCount    = 0, lateCount = 0, earlyLeaveCount = 0, leaveCount = 0;
        double totalWorkHours = 0, totalOvertimeHours = 0;

        for (AttendanceRecord r : todayRecords) {
            if (r.getCheckInTime() != null) {
                checkedInCount++;
                if (r.getCheckOutTime() != null) {
                    checkedOutCount++;
                } else {
                    stillWorkingCount++;
                }
            }
            if (r.getWorkHours() != null)    totalWorkHours    += r.getWorkHours().doubleValue();
            if (r.getOvertimeHours() != null) totalOvertimeHours += r.getOvertimeHours().doubleValue();

            if (r.getStatus() == null) continue;
            switch (r.getStatus()) {
                case PRESENT     -> presentCount++;
                case LATE        -> { presentCount++; lateCount++; }
                case EARLY_LEAVE -> { presentCount++; earlyLeaveCount++; }
                case LEAVE       -> leaveCount++;
                default          -> {}
            }
        }

        int absentCount       = Math.max(0, totalEmployees - checkedInCount - leaveCount);
        int notCheckedInCount = Math.max(0, totalEmployees - checkedInCount);
        double attendanceRate = totalEmployees > 0
                ? Math.round((double) checkedInCount / totalEmployees * 100 * 10) / 10.0
                : 0.0;

        // Anomalies
        long unresolvedAnomalies = anomalyRepository.countUnresolved();
        long criticalAnomalies   = anomalyRepository.countUnresolvedCritical();

        // Pending approvals (trong 30 ngày gần nhất)
        long pendingApprovals = attendanceRecordRepository
                .countPendingApprovals(today.minusDays(30));

        // Weekly trend (7 ngày qua)
        List<DashboardDTO.DailyTrendDTO> weeklyTrend =
                buildWeeklyTrend(today, allEmployees);

        // Department ranking
        List<DashboardDTO.DepartmentRankDTO> deptRanking =
                buildDepartmentRanking(today, allEmployees);

        // Recent activities (10 hoạt động gần nhất)
        List<DashboardDTO.RecentActivityDTO> recentActivities =
                buildRecentActivities(today, allEmployees);

        return DashboardDTO.builder()
                .date(today)
                .generatedAt(now)
                .totalEmployees(totalEmployees)
                .checkedInCount(checkedInCount)
                .checkedOutCount(checkedOutCount)
                .notCheckedInCount(notCheckedInCount)
                .stillWorkingCount(stillWorkingCount)
                .attendanceRate(attendanceRate)
                .presentCount(presentCount)
                .lateCount(lateCount)
                .absentCount(absentCount)
                .leaveCount(leaveCount)
                .earlyLeaveCount(earlyLeaveCount)
                .totalWorkHoursToday(Math.round(totalWorkHours * 100.0) / 100.0)
                .totalOvertimeHoursToday(Math.round(totalOvertimeHours * 100.0) / 100.0)
                .unresolvedAnomalies((int) unresolvedAnomalies)
                .criticalAnomalies((int) criticalAnomalies)
                .pendingApprovals((int) pendingApprovals)
                .weeklyTrend(weeklyTrend)
                .departmentRanking(deptRanking)
                .recentActivities(recentActivities)
                .build();
    }

    // ============================================
    // DAILY REPORT
    // ============================================

    @Override
    public CompanyDailyReportDTO getDailyReport(LocalDate date) {
        log.info("[REPORT] Daily report for date={}", date);

        List<Employee> allEmployees = employeeRepository.findAll().stream()
                .filter(e -> !Boolean.TRUE.equals(e.getIsDeleted()))
                .collect(Collectors.toList());

        List<Department> departments = departmentRepository.findByIsDeletedFalse();

        List<AttendanceRecord> records = attendanceRecordRepository.findAllByDate(date);

        // Map employeeId → record
        Map<Integer, AttendanceRecord> recordMap = records.stream()
                .collect(Collectors.toMap(AttendanceRecord::getEmployeeId, Function.identity()));

        // Tổng hợp theo phòng ban
        List<DepartmentSummaryDTO> byDepartment = new ArrayList<>();

        int totalPresent = 0, totalAbsent = 0, totalLate = 0;
        int totalEarlyLeave = 0, totalLeave = 0, totalNotChecked = 0;
        double totalWorkHours = 0, totalOvertimeHours = 0;

        for (Department dept : departments) {
            List<Employee> deptEmps = allEmployees.stream()
                    .filter(e -> dept.getId().equals(e.getDeptId()))
                    .collect(Collectors.toList());

            if (deptEmps.isEmpty()) continue;

            int dPresent = 0, dLate = 0, dEarlyLeave = 0, dLeave = 0, dNotCheckedOut = 0;
            double dWorkHours = 0, dOvertimeHours = 0;

            for (Employee emp : deptEmps) {
                AttendanceRecord r = recordMap.get(emp.getId());
                if (r == null) continue;

                if (r.getStatus() != null) {
                    switch (r.getStatus()) {
                        case PRESENT     -> dPresent++;
                        case LATE        -> { dPresent++; dLate++; }
                        case EARLY_LEAVE -> { dPresent++; dEarlyLeave++; }
                        case LEAVE       -> dLeave++;
                        default          -> {}
                    }
                }
                if (r.getCheckInTime() != null && r.getCheckOutTime() == null) dNotCheckedOut++;
                if (r.getWorkHours() != null)    dWorkHours    += r.getWorkHours().doubleValue();
                if (r.getOvertimeHours() != null) dOvertimeHours += r.getOvertimeHours().doubleValue();
            }

            int dAbsent = Math.max(0, deptEmps.size() - dPresent - dLeave);
            int dNotCheckedIn = deptEmps.size() - (int) deptEmps.stream()
                    .filter(e -> recordMap.containsKey(e.getId())).count();

            double dAttendanceRate = deptEmps.size() > 0
                    ? Math.round((double) dPresent / deptEmps.size() * 100 * 10) / 10.0 : 0;
            double dPunctualityRate = dPresent > 0
                    ? Math.round((double) (dPresent - dLate) / dPresent * 100 * 10) / 10.0 : 0;

            byDepartment.add(DepartmentSummaryDTO.builder()
                    .deptId(dept.getId())
                    .deptName(dept.getDeptName())
                    .date(date)
                    .totalEmployees(deptEmps.size())
                    .presentCount(dPresent)
                    .absentCount(dAbsent)
                    .lateCount(dLate)
                    .earlyLeaveCount(dEarlyLeave)
                    .leaveCount(dLeave)
                    .notCheckedInCount(dNotCheckedIn)
                    .notCheckedOutCount(dNotCheckedOut)
                    .attendanceRate(dAttendanceRate)
                    .punctualityRate(dPunctualityRate)
                    .totalWorkHours(Math.round(dWorkHours * 100.0) / 100.0)
                    .totalOvertimeHours(Math.round(dOvertimeHours * 100.0) / 100.0)
                    .build());

            totalPresent     += dPresent;
            totalLate        += dLate;
            totalEarlyLeave  += dEarlyLeave;
            totalLeave       += dLeave;
            totalWorkHours   += dWorkHours;
            totalOvertimeHours += dOvertimeHours;
        }

        totalAbsent = Math.max(0, allEmployees.size() - totalPresent - totalLeave);
        totalNotChecked = allEmployees.size() - records.size();

        double companyAttendanceRate = allEmployees.size() > 0
                ? Math.round((double) totalPresent / allEmployees.size() * 100 * 10) / 10.0 : 0;
        double companyPunctualityRate = totalPresent > 0
                ? Math.round((double) (totalPresent - totalLate) / totalPresent * 100 * 10) / 10.0 : 0;

        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        return CompanyDailyReportDTO.builder()
                .date(date)
                .dayOfWeek(dayOfWeek)
                .totalEmployees(allEmployees.size())
                .presentCount(totalPresent)
                .absentCount(totalAbsent)
                .lateCount(totalLate)
                .earlyLeaveCount(totalEarlyLeave)
                .leaveCount(totalLeave)
                .notCheckedCount(totalNotChecked)
                .attendanceRate(companyAttendanceRate)
                .punctualityRate(companyPunctualityRate)
                .totalWorkHours(Math.round(totalWorkHours * 100.0) / 100.0)
                .totalOvertimeHours(Math.round(totalOvertimeHours * 100.0) / 100.0)
                .byDepartment(byDepartment)
                .build();
    }

    // ============================================
    // MONTHLY REPORT
    // ============================================

    @Override
    public CompanyMonthlyReportDTO getMonthlyReport(int year, int month) {
        log.info("[REPORT] Monthly report: year={}, month={}", year, month);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate   = yearMonth.atEndOfMonth();

        List<Employee> allEmployees = employeeRepository.findAll().stream()
                .filter(e -> !Boolean.TRUE.equals(e.getIsDeleted()))
                .collect(Collectors.toList());

        List<Department> departments = departmentRepository.findByIsDeletedFalse();

        List<Integer> allEmployeeIds = allEmployees.stream()
                .map(Employee::getId).collect(Collectors.toList());

        List<AttendanceRecord> allRecords = allEmployeeIds.isEmpty()
                ? Collections.emptyList()
                : attendanceRecordRepository.findByEmployeeIdsAndDateBetween(
                        allEmployeeIds, startDate, endDate);

        // Group by employeeId
        Map<Integer, List<AttendanceRecord>> byEmployee = allRecords.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getEmployeeId));

        // Map deptId → employees
        Map<Integer, List<Employee>> empByDept = allEmployees.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDeptId() != null ? e.getDeptId() : -1
                ));

        // Build per-department stats
        List<CompanyMonthlyReportDTO.DepartmentMonthlyStatDTO> deptStats = new ArrayList<>();
        double totalWorkHours = 0, totalOvertimeHours = 0;
        double totalAttendanceRate = 0;
        int deptWithEmployees = 0;

        for (Department dept : departments) {
            List<Employee> deptEmps = empByDept.getOrDefault(dept.getId(), Collections.emptyList());
            if (deptEmps.isEmpty()) continue;
            deptWithEmployees++;

            int dTotalLateDays = 0, dTotalAbsentDays = 0, dPresentSlots = 0;
            double dWorkHours = 0, dOvertimeHours = 0;

            for (Employee emp : deptEmps) {
                List<AttendanceRecord> empRecords = byEmployee.getOrDefault(
                        emp.getId(), Collections.emptyList());

                for (AttendanceRecord r : empRecords) {
                    if (r.getStatus() == AttendanceStatus.LATE) dTotalLateDays++;
                    if (r.getStatus() == AttendanceStatus.PRESENT
                            || r.getStatus() == AttendanceStatus.LATE
                            || r.getStatus() == AttendanceStatus.EARLY_LEAVE) dPresentSlots++;
                    if (r.getWorkHours() != null)    dWorkHours    += r.getWorkHours().doubleValue();
                    if (r.getOvertimeHours() != null) dOvertimeHours += r.getOvertimeHours().doubleValue();
                }
            }

            // Tính work days trong tháng (Mon-Fri mặc định)
            int workDaysInMonth = countWorkDaysInMonth(yearMonth, List.of(1, 2, 3, 4, 5));
            int maxSlots = deptEmps.size() * workDaysInMonth;
            dTotalAbsentDays = Math.max(0, maxSlots - dPresentSlots);

            double dAttendRate = maxSlots > 0
                    ? Math.round((double) dPresentSlots / maxSlots * 100 * 10) / 10.0 : 0;

            deptStats.add(CompanyMonthlyReportDTO.DepartmentMonthlyStatDTO.builder()
                    .deptId(dept.getId())
                    .deptName(dept.getDeptName())
                    .totalEmployees(deptEmps.size())
                    .avgAttendanceRate(dAttendRate)
                    .totalWorkHours(Math.round(dWorkHours * 100.0) / 100.0)
                    .totalOvertimeHours(Math.round(dOvertimeHours * 100.0) / 100.0)
                    .totalLateDays(dTotalLateDays)
                    .totalAbsentDays(dTotalAbsentDays)
                    .build());

            totalAttendanceRate += dAttendRate;
            totalWorkHours      += dWorkHours;
            totalOvertimeHours  += dOvertimeHours;
        }

        // Sort theo attendance rate (cao → thấp)
        deptStats.sort(Comparator.comparingDouble(
                CompanyMonthlyReportDTO.DepartmentMonthlyStatDTO::getAvgAttendanceRate
        ).reversed());

        // Top performers
        List<CompanyMonthlyReportDTO.TopPerformerDTO> topAttendance =
                buildTopAttendance(year, month, allEmployees, byEmployee, departments);
        List<CompanyMonthlyReportDTO.TopPerformerDTO> topLate =
                buildTopLate(year, month, allEmployees, byEmployee, departments);

        double avgAttendanceRate = deptWithEmployees > 0
                ? Math.round(totalAttendanceRate / deptWithEmployees * 10) / 10.0 : 0;

        int workDaysInMonth = countWorkDaysInMonth(yearMonth, List.of(1, 2, 3, 4, 5));
        int totalPresentSlots = (int) allRecords.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.PRESENT
                        || r.getStatus() == AttendanceStatus.LATE
                        || r.getStatus() == AttendanceStatus.EARLY_LEAVE)
                .count();

        return CompanyMonthlyReportDTO.builder()
                .year(year)
                .month(month)
                .monthName(Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                .totalEmployees(allEmployees.size())
                .avgAttendanceRate(avgAttendanceRate)
                .totalWorkHours(Math.round(totalWorkHours * 100.0) / 100.0)
                .totalOvertimeHours(Math.round(totalOvertimeHours * 100.0) / 100.0)
                .totalWorkDaysInMonth(workDaysInMonth)
                .totalPresentSlots(totalPresentSlots)
                .totalAbsentSlots(Math.max(0, allEmployees.size() * workDaysInMonth - totalPresentSlots))
                .byDepartment(deptStats)
                .topAttendance(topAttendance)
                .topLate(topLate)
                .build();
    }

    // ============================================
    // DEPARTMENT COMPARISON
    // ============================================

    @Override
    public DepartmentComparisonDTO getDepartmentComparison(
            LocalDate startDate, LocalDate endDate) {

        log.info("[REPORT] Department comparison: {} to {}", startDate, endDate);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before endDate");
        }

        List<Department> departments = departmentRepository.findByIsDeletedFalse();
        List<Employee> allEmployees = employeeRepository.findAll().stream()
                .filter(e -> !Boolean.TRUE.equals(e.getIsDeleted()))
                .collect(Collectors.toList());

        Map<Integer, List<Employee>> empByDept = allEmployees.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDeptId() != null ? e.getDeptId() : -1
                ));

        List<DepartmentComparisonDTO.DeptCompareItemDTO> items = new ArrayList<>();

        for (Department dept : departments) {
            List<Employee> deptEmps = empByDept.getOrDefault(dept.getId(), Collections.emptyList());
            if (deptEmps.isEmpty()) continue;

            List<Integer> deptEmpIds = deptEmps.stream()
                    .map(Employee::getId).collect(Collectors.toList());

            List<AttendanceRecord> deptRecords = attendanceRecordRepository
                    .findByEmployeeIdsAndDateBetween(deptEmpIds, startDate, endDate);

            // Phân tích
            int presentDays = 0, absentDays = 0, lateDays = 0;
            double workHours = 0, overtimeHours = 0;

            for (AttendanceRecord r : deptRecords) {
                if (r.getStatus() == null) continue;
                switch (r.getStatus()) {
                    case PRESENT     -> presentDays++;
                    case LATE        -> { presentDays++; lateDays++; }
                    case EARLY_LEAVE -> presentDays++;
                    default          -> {}
                }
                if (r.getWorkHours() != null)    workHours    += r.getWorkHours().doubleValue();
                if (r.getOvertimeHours() != null) overtimeHours += r.getOvertimeHours().doubleValue();
            }

            // Tính work days trong khoảng
            long workDaysInRange = startDate.datesUntil(endDate.plusDays(1))
                    .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY
                              && d.getDayOfWeek() != DayOfWeek.SUNDAY)
                    .count();

            int maxSlots = (int) (deptEmps.size() * workDaysInRange);
            absentDays = Math.max(0, maxSlots - presentDays);

            double attendanceRate = maxSlots > 0
                    ? Math.round((double) presentDays / maxSlots * 100 * 10) / 10.0 : 0;
            double punctualityRate = presentDays > 0
                    ? Math.round((double) (presentDays - lateDays) / presentDays * 100 * 10) / 10.0 : 0;
            double avgWorkHoursPerDay = workDaysInRange > 0
                    ? Math.round(workHours / workDaysInRange * 100.0) / 100.0 : 0;

            items.add(DepartmentComparisonDTO.DeptCompareItemDTO.builder()
                    .deptId(dept.getId())
                    .deptName(dept.getDeptName())
                    .totalEmployees(deptEmps.size())
                    .attendanceRate(attendanceRate)
                    .punctualityRate(punctualityRate)
                    .totalPresentDays(presentDays)
                    .totalAbsentDays(absentDays)
                    .totalLateDays(lateDays)
                    .totalWorkHours(Math.round(workHours * 100.0) / 100.0)
                    .totalOvertimeHours(Math.round(overtimeHours * 100.0) / 100.0)
                    .avgWorkHoursPerDay(avgWorkHoursPerDay)
                    .build());
        }

        // Sort và gán rank
        items.sort(Comparator.comparingDouble(
                DepartmentComparisonDTO.DeptCompareItemDTO::getAttendanceRate
        ).reversed());

        for (int i = 0; i < items.size(); i++) {
            items.get(i).setRank(i + 1);
        }

        String best  = items.isEmpty() ? null : items.get(0).getDeptName();
        String worst = items.isEmpty() ? null : items.get(items.size() - 1).getDeptName();

        return DepartmentComparisonDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalDepartments(items.size())
                .departments(items)
                .bestDepartment(best)
                .worstDepartment(worst)
                .build();
    }

    // ============================================
    // EXPORT EXCEL - DEPARTMENT MONTHLY
    // ============================================

    @Override
    public void exportDepartmentMonthlyExcel(
            Integer deptId, int year, int month,
            HttpServletResponse response) throws IOException {

        log.info("[REPORT] Exporting Excel: deptId={}, year={}, month={}", deptId, year, month);

        Department dept = departmentRepository.findByIdAndIsDeletedFalse(deptId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + deptId));

        List<Employee> employees = employeeRepository.findByDeptIdAndIsDeletedFalse(deptId);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate   = yearMonth.atEndOfMonth();
        int totalDays       = yearMonth.lengthOfMonth();

        List<Integer> empIds = employees.stream()
                .map(Employee::getId).collect(Collectors.toList());

        List<AttendanceRecord> records = empIds.isEmpty()
                ? Collections.emptyList()
                : attendanceRecordRepository.findByEmployeeIdsAndDateBetween(empIds, startDate, endDate);

        Map<Integer, Map<LocalDate, AttendanceRecord>> recordMap = new HashMap<>();
        for (AttendanceRecord r : records) {
            recordMap
                .computeIfAbsent(r.getEmployeeId(), k -> new HashMap<>())
                .put(r.getDate(), r);
        }

        // Tên file
        String fileName = String.format("attendance_%s_%d_%02d.xlsx",
                dept.getDeptName().replaceAll("\\s+", "_"), year, month);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance");

            // ====== Styles ======
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle  = createTitleStyle(workbook);
            CellStyle dataStyle   = createDataStyle(workbook);
            CellStyle presentStyle    = createColorStyle(workbook, IndexedColors.LIGHT_GREEN);
            CellStyle absentStyle     = createColorStyle(workbook, IndexedColors.ROSE);
            CellStyle lateStyle       = createColorStyle(workbook, IndexedColors.LIGHT_YELLOW);
            CellStyle weekendStyle    = createColorStyle(workbook, IndexedColors.GREY_25_PERCENT);

            int rowNum = 0;

            // ====== Title ======
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(String.format("BẢNG CHẤM CÔNG - %s - %s/%d",
                    dept.getDeptName().toUpperCase(),
                    Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase(),
                    year));
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, totalDays + 5));
            rowNum++; // blank row

            // ====== Header Row ======
            Row headerRow = sheet.createRow(rowNum++);
            String[] fixedHeaders = {"#", "Employee Name", "Department", "Shift"};

            for (int i = 0; i < fixedHeaders.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(fixedHeaders[i]);
                c.setCellStyle(headerStyle);
            }

            // Cột ngày (1 → totalDays)
            for (int d = 1; d <= totalDays; d++) {
                LocalDate date = LocalDate.of(year, month, d);
                Cell c = headerRow.createCell(fixedHeaders.length + d - 1);
                String dayLabel = d + "\n"
                        + date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                c.setCellValue(dayLabel);
                c.setCellStyle(headerStyle);
            }

            // Cột tổng hợp
            String[] summaryHeaders = {"Present", "Absent", "Late", "Work Hours"};
            for (int i = 0; i < summaryHeaders.length; i++) {
                Cell c = headerRow.createCell(fixedHeaders.length + totalDays + i);
                c.setCellValue(summaryHeaders[i]);
                c.setCellStyle(headerStyle);
            }

            // ====== Data Rows ======
            int idx = 1;
            for (Employee emp : employees) {
                Row row = sheet.createRow(rowNum++);
                Map<LocalDate, AttendanceRecord> empMap =
                        recordMap.getOrDefault(emp.getId(), Collections.emptyMap());

                // Fixed columns
                row.createCell(0).setCellValue(idx++);
                Cell nameCell = row.createCell(1);
                nameCell.setCellValue(emp.getFullName());
                nameCell.setCellStyle(dataStyle);

                Cell deptCell = row.createCell(2);
                deptCell.setCellValue(dept.getDeptName());
                deptCell.setCellStyle(dataStyle);

                String shiftName = emp.getShift() != null ? emp.getShift().getName() : "-";
                Cell shiftCell = row.createCell(3);
                shiftCell.setCellValue(shiftName);
                shiftCell.setCellStyle(dataStyle);

                // Daily columns
                int presentDays = 0, absentDays = 0, lateDays = 0;
                double totalWorkHours = 0;

                for (int d = 1; d <= totalDays; d++) {
                    LocalDate date = LocalDate.of(year, month, d);
                    Cell c = row.createCell(fixedHeaders.length + d - 1);

                    boolean isWeekend = date.getDayOfWeek() == DayOfWeek.SATURDAY
                                     || date.getDayOfWeek() == DayOfWeek.SUNDAY;

                    if (isWeekend) {
                        c.setCellValue("-");
                        c.setCellStyle(weekendStyle);
                        continue;
                    }

                    AttendanceRecord r = empMap.get(date);
                    if (r == null) {
                        // Ngày đã qua → ABSENT
                        if (date.isBefore(LocalDate.now())) {
                            c.setCellValue("A");
                            c.setCellStyle(absentStyle);
                            absentDays++;
                        } else {
                            c.setCellValue("");
                            c.setCellStyle(dataStyle);
                        }
                    } else {
                        String statusCode = getStatusCode(r.getStatus());
                        c.setCellValue(statusCode);

                        if (r.getStatus() == AttendanceStatus.PRESENT
                                || r.getStatus() == AttendanceStatus.EARLY_LEAVE) {
                            c.setCellStyle(presentStyle);
                            presentDays++;
                        } else if (r.getStatus() == AttendanceStatus.LATE) {
                            c.setCellStyle(lateStyle);
                            presentDays++;
                            lateDays++;
                        } else if (r.getStatus() == AttendanceStatus.ABSENT) {
                            c.setCellStyle(absentStyle);
                            absentDays++;
                        } else {
                            c.setCellStyle(dataStyle);
                        }

                        if (r.getWorkHours() != null) {
                            totalWorkHours += r.getWorkHours().doubleValue();
                        }
                    }
                }

                // Summary columns
                row.createCell(fixedHeaders.length + totalDays).setCellValue(presentDays);
                row.createCell(fixedHeaders.length + totalDays + 1).setCellValue(absentDays);
                row.createCell(fixedHeaders.length + totalDays + 2).setCellValue(lateDays);
                row.createCell(fixedHeaders.length + totalDays + 3)
                        .setCellValue(Math.round(totalWorkHours * 10.0) / 10.0);
            }

            // ====== Auto-size columns ======
            sheet.setColumnWidth(0, 8 * 256);   // #
            sheet.setColumnWidth(1, 25 * 256);  // Name
            sheet.setColumnWidth(2, 20 * 256);  // Dept
            sheet.setColumnWidth(3, 15 * 256);  // Shift
            for (int d = 0; d < totalDays; d++) {
                sheet.setColumnWidth(fixedHeaders.length + d, 5 * 256);
            }
            for (int s = 0; s < summaryHeaders.length; s++) {
                sheet.setColumnWidth(fixedHeaders.length + totalDays + s, 12 * 256);
            }

            workbook.write(response.getOutputStream());
        }

        log.info("[REPORT] Excel exported successfully for dept={}", deptId);
    }

    // ============================================
    // EXPORT EXCEL - COMPANY MONTHLY
    // ============================================

    @Override
    public void exportCompanyMonthlyExcel(
            int year, int month,
            HttpServletResponse response) throws IOException {

        log.info("[REPORT] Exporting company Excel: year={}, month={}", year, month);

        CompanyMonthlyReportDTO report = getMonthlyReport(year, month);

        String fileName = String.format("company_attendance_%d_%02d.xlsx", year, month);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Company Report");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle  = createTitleStyle(workbook);
            CellStyle dataStyle   = createDataStyle(workbook);

            int rowNum = 0;

            // Title
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(String.format("COMPANY ATTENDANCE REPORT - %s/%d",
                    Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase(), year));
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
            rowNum++;

            // Summary row
            Row summaryRow = sheet.createRow(rowNum++);
            summaryRow.createCell(0).setCellValue("Total Employees:");
            summaryRow.createCell(1).setCellValue(report.getTotalEmployees());
            summaryRow.createCell(3).setCellValue("Avg Attendance Rate:");
            summaryRow.createCell(4).setCellValue(report.getAvgAttendanceRate() + "%");
            summaryRow.createCell(6).setCellValue("Total Work Hours:");
            summaryRow.createCell(7).setCellValue(report.getTotalWorkHours());
            rowNum++;

            // Header
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {
                "Department", "Employees", "Attendance Rate (%)",
                "Present Days", "Absent Days", "Late Days",
                "Work Hours", "Overtime Hours"
            };
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            // Data rows
            for (CompanyMonthlyReportDTO.DepartmentMonthlyStatDTO dept : report.getByDepartment()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(dept.getDeptName());
                row.createCell(1).setCellValue(dept.getTotalEmployees());
                row.createCell(2).setCellValue(dept.getAvgAttendanceRate());
                row.createCell(3).setCellValue(dept.getTotalWorkHours() > 0
                        ? (int)(dept.getTotalWorkHours() / 8) : 0); // Estimate present days
                row.createCell(4).setCellValue(dept.getTotalAbsentDays());
                row.createCell(5).setCellValue(dept.getTotalLateDays());
                row.createCell(6).setCellValue(dept.getTotalWorkHours());
                row.createCell(7).setCellValue(dept.getTotalOvertimeHours());

                for (int i = 0; i < headers.length; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            // Auto-size
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    // ============================================
    // PRIVATE HELPERS
    // ============================================

    private List<DashboardDTO.DailyTrendDTO> buildWeeklyTrend(
            LocalDate today, List<Employee> allEmployees) {

        List<DashboardDTO.DailyTrendDTO> trend = new ArrayList<>();
        int totalEmp = allEmployees.size();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            List<AttendanceRecord> dayRecords = attendanceRecordRepository.findAllByDate(date);

            int present = (int) dayRecords.stream()
                    .filter(r -> r.getStatus() == AttendanceStatus.PRESENT
                              || r.getStatus() == AttendanceStatus.LATE
                              || r.getStatus() == AttendanceStatus.EARLY_LEAVE)
                    .count();
            int absent = Math.max(0, totalEmp - (int) dayRecords.stream()
                    .filter(r -> r.getCheckInTime() != null).count());
            int late = (int) dayRecords.stream()
                    .filter(r -> r.getStatus() == AttendanceStatus.LATE).count();

            double rate = totalEmp > 0
                    ? Math.round((double) present / totalEmp * 100 * 10) / 10.0 : 0;

            trend.add(DashboardDTO.DailyTrendDTO.builder()
                    .date(date)
                    .dayOfWeek(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                    .present(present)
                    .absent(absent)
                    .late(late)
                    .attendanceRate(rate)
                    .build());
        }

        return trend;
    }

    private List<DashboardDTO.DepartmentRankDTO> buildDepartmentRanking(
            LocalDate today, List<Employee> allEmployees) {

        List<Department> departments = departmentRepository.findByIsDeletedFalse();
        List<AttendanceRecord> todayRecords = attendanceRecordRepository.findAllByDate(today);

        Map<Integer, AttendanceRecord> recordMap = todayRecords.stream()
                .collect(Collectors.toMap(AttendanceRecord::getEmployeeId, Function.identity(),
                        (a, b) -> a));

        Map<Integer, List<Employee>> empByDept = allEmployees.stream()
                .filter(e -> e.getDeptId() != null)
                .collect(Collectors.groupingBy(Employee::getDeptId));

        List<DashboardDTO.DepartmentRankDTO> ranking = new ArrayList<>();

        for (Department dept : departments) {
            List<Employee> deptEmps = empByDept.getOrDefault(dept.getId(), Collections.emptyList());
            if (deptEmps.isEmpty()) continue;

            int present = 0, late = 0;
            for (Employee emp : deptEmps) {
                AttendanceRecord r = recordMap.get(emp.getId());
                if (r == null || r.getCheckInTime() == null) continue;
                present++;
                if (r.getStatus() == AttendanceStatus.LATE) late++;
            }

            double rate = deptEmps.size() > 0
                    ? Math.round((double) present / deptEmps.size() * 100 * 10) / 10.0 : 0;

            ranking.add(DashboardDTO.DepartmentRankDTO.builder()
                    .deptId(dept.getId())
                    .deptName(dept.getDeptName())
                    .totalEmployees(deptEmps.size())
                    .presentCount(present)
                    .attendanceRate(rate)
                    .lateCount(late)
                    .build());
        }

        // Sort cao → thấp
        ranking.sort(Comparator.comparingDouble(
                DashboardDTO.DepartmentRankDTO::getAttendanceRate).reversed());

        return ranking;
    }

    private List<DashboardDTO.RecentActivityDTO> buildRecentActivities(
            LocalDate today, List<Employee> allEmployees) {

        Map<Integer, Employee> empMap = allEmployees.stream()
                .collect(Collectors.toMap(Employee::getId, Function.identity()));

        List<Department> departments = departmentRepository.findByIsDeletedFalse();
        Map<Integer, String> deptNameMap = departments.stream()
                .collect(Collectors.toMap(Department::getId, Department::getDeptName));

        List<AttendanceRecord> recent = attendanceRecordRepository
                .findRecentActivities(today, PageRequest.of(0, 10));

        List<DashboardDTO.RecentActivityDTO> activities = new ArrayList<>();

        for (AttendanceRecord r : recent) {
            Employee emp = empMap.get(r.getEmployeeId());
            if (emp == null) continue;

            String deptName = emp.getDeptId() != null
                    ? deptNameMap.getOrDefault(emp.getDeptId(), "-") : "-";

            // Check-out activity
            if (r.getCheckOutTime() != null) {
                activities.add(DashboardDTO.RecentActivityDTO.builder()
                        .attendanceId(r.getId())
                        .employeeId(emp.getId())
                        .employeeName(emp.getFullName())
                        .deptName(deptName)
                        .action("CHECK_OUT")
                        .time(r.getCheckOutTime())
                        .status(r.getStatus() != null ? r.getStatus().name() : null)
                        .faceMatchScore(r.getCheckOutFaceMatchScore() != null
                                ? r.getCheckOutFaceMatchScore().doubleValue() : null)
                        .build());
            }

            // Check-in activity
            if (r.getCheckInTime() != null) {
                activities.add(DashboardDTO.RecentActivityDTO.builder()
                        .attendanceId(r.getId())
                        .employeeId(emp.getId())
                        .employeeName(emp.getFullName())
                        .deptName(deptName)
                        .action("CHECK_IN")
                        .time(r.getCheckInTime())
                        .status(r.getStatus() != null ? r.getStatus().name() : null)
                        .faceMatchScore(r.getCheckInFaceMatchScore() != null
                                ? r.getCheckInFaceMatchScore().doubleValue() : null)
                        .build());
            }
        }

        // Sort by time DESC, lấy 10 cái gần nhất
        activities.sort(Comparator.comparing(
                DashboardDTO.RecentActivityDTO::getTime,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));

        return activities.stream().limit(10).collect(Collectors.toList());
    }

    private List<CompanyMonthlyReportDTO.TopPerformerDTO> buildTopAttendance(
            int year, int month,
            List<Employee> allEmployees,
            Map<Integer, List<AttendanceRecord>> byEmployee,
            List<Department> departments) {

        Map<Integer, String> deptNameMap = departments.stream()
                .collect(Collectors.toMap(Department::getId, Department::getDeptName));

        return allEmployees.stream()
                .map(emp -> {
                    List<AttendanceRecord> records = byEmployee
                            .getOrDefault(emp.getId(), Collections.emptyList());
                    int presentDays = (int) records.stream()
                            .filter(r -> r.getStatus() == AttendanceStatus.PRESENT
                                      || r.getStatus() == AttendanceStatus.LATE
                                      || r.getStatus() == AttendanceStatus.EARLY_LEAVE)
                            .count();

                    String deptName = emp.getDeptId() != null
                            ? deptNameMap.getOrDefault(emp.getDeptId(), "-") : "-";

                    return CompanyMonthlyReportDTO.TopPerformerDTO.builder()
                            .employeeId(emp.getId())
                            .fullName(emp.getFullName())
                            .deptName(deptName)
                            .value(presentDays)
                            .build();
                })
                .sorted(Comparator.comparingInt(
                        CompanyMonthlyReportDTO.TopPerformerDTO::getValue).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    private List<CompanyMonthlyReportDTO.TopPerformerDTO> buildTopLate(
            int year, int month,
            List<Employee> allEmployees,
            Map<Integer, List<AttendanceRecord>> byEmployee,
            List<Department> departments) {

        Map<Integer, String> deptNameMap = departments.stream()
                .collect(Collectors.toMap(Department::getId, Department::getDeptName));

        return allEmployees.stream()
                .map(emp -> {
                    List<AttendanceRecord> records = byEmployee
                            .getOrDefault(emp.getId(), Collections.emptyList());
                    int lateDays = (int) records.stream()
                            .filter(r -> r.getStatus() == AttendanceStatus.LATE)
                            .count();

                    String deptName = emp.getDeptId() != null
                            ? deptNameMap.getOrDefault(emp.getDeptId(), "-") : "-";

                    return CompanyMonthlyReportDTO.TopPerformerDTO.builder()
                            .employeeId(emp.getId())
                            .fullName(emp.getFullName())
                            .deptName(deptName)
                            .value(lateDays)
                            .build();
                })
                .filter(dto -> dto.getValue() > 0)
                .sorted(Comparator.comparingInt(
                        CompanyMonthlyReportDTO.TopPerformerDTO::getValue).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Đếm số ngày làm việc trong tháng dựa theo workDays
     */
    private int countWorkDaysInMonth(YearMonth yearMonth, List<Integer> workDays) {
        int count = 0;
        for (int d = 1; d <= yearMonth.lengthOfMonth(); d++) {
            LocalDate date = yearMonth.atDay(d);
            if (workDays.contains(date.getDayOfWeek().getValue())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Chuyển AttendanceStatus → ký hiệu hiển thị trong Excel
     */
    private String getStatusCode(AttendanceStatus status) {
        if (status == null) return "";
        return switch (status) {
            case PRESENT     -> "P";
            case LATE        -> "L";
            case EARLY_LEAVE -> "E";
            case ABSENT      -> "A";
            case LEAVE       -> "AL";
            case NOT_CHECKED -> "NC";
        };
    }

    // ====== Excel Style Helpers ======

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        XSSFFont whiteFont = (XSSFFont) workbook.createFont();
        whiteFont.setBold(true);
        whiteFont.setColor(IndexedColors.WHITE.getIndex());
        whiteFont.setFontHeightInPoints((short) 11);
        style.setFont(whiteFont);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createColorStyle(Workbook workbook, IndexedColors color) {
        CellStyle style = createDataStyle(workbook);
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}