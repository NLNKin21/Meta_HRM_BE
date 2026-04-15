package com.metahrms.employee_management.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metahrms.employee_management.dto.request.Attendance.AttendanceRecordDTO;
import com.metahrms.employee_management.dto.response.Attendance.DayRecordDTO;
import com.metahrms.employee_management.dto.response.Attendance.MyAttendanceSummaryDTO;
import com.metahrms.employee_management.dto.response.Attendance.MyMonthlyCalendarDTO;
import com.metahrms.employee_management.dto.response.Attendance.MyTodayStatusDTO;
import com.metahrms.employee_management.entity.Attendance.AttendanceRecord;
import com.metahrms.employee_management.entity.Attendance.Shift;
import com.metahrms.employee_management.entity.Attendance.WorkLocation;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.enums.Attendance.AttendanceStatus;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.Attendance.AttendanceRecordRepository;
import com.metahrms.employee_management.repository.Attendance.ShiftRepository;
import com.metahrms.employee_management.repository.Attendance.WorkLocationRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.service.MyAttendanceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyAttendanceServiceImpl implements MyAttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EmployeeRepository employeeRepository;
    private final ShiftRepository shiftRepository;
    private final WorkLocationRepository workLocationRepository;
    private final ObjectMapper objectMapper;

    // ============================================
    // GET TODAY STATUS
    // ============================================

    @Override
    public MyTodayStatusDTO getTodayStatus(Integer userId) {
        log.info("[MY-ATTENDANCE] Getting today status for userId={}", userId);

        Employee employee = getEmployeeByUserId(userId);
        LocalDate today = LocalDate.now();

        // Lấy shift hiện tại
        Shift shift = getEmployeeShift(employee);

        // Lấy attendance record hôm nay
        Optional<AttendanceRecord> recordOpt = attendanceRecordRepository
                .findByEmployeeIdAndDate(employee.getId(), today);

        // Build response
        MyTodayStatusDTO.MyTodayStatusDTOBuilder builder = MyTodayStatusDTO.builder()
                .date(today)
                .hasCheckedIn(false)
                .hasCheckedOut(false);

        // Thêm shift info
        if (shift != null) {
            builder.shiftName(shift.getName())
                   .shiftStartTime(shift.getStartTime().toString())
                   .shiftEndTime(shift.getEndTime().toString());
        }

        // Thêm attendance info nếu có record
        if (recordOpt.isPresent()) {
            AttendanceRecord record = recordOpt.get();

            boolean checkedIn = record.getCheckInTime() != null;
            boolean checkedOut = record.getCheckOutTime() != null;

            builder.hasCheckedIn(checkedIn)
                   .hasCheckedOut(checkedOut)
                   .checkInTime(record.getCheckInTime())
                   .checkOutTime(record.getCheckOutTime())
                   .status(record.getStatus())
                   .lateMinutes(record.getLateMinutes())
                   .workHours(record.getWorkHours() != null
                           ? record.getWorkHours().doubleValue() : null)
                   .isVerified(record.getIsVerified())
                   .isApproved(record.getIsApproved());

            // Tính giờ làm tính đến hiện tại (nếu chưa check-out)
            if (checkedIn && !checkedOut) {
                Duration durationSoFar = Duration.between(
                        record.getCheckInTime(), LocalDateTime.now()
                );
                double hoursSoFar = durationSoFar.toMinutes() / 60.0;
                builder.workHoursUntilNow(Math.round(hoursSoFar * 100.0) / 100.0);
            }

            // Tên location check-in
            if (record.getCheckInLocationId() != null) {
                workLocationRepository.findById(record.getCheckInLocationId())
                        .ifPresent(loc -> builder.checkInLocationName(loc.getName()));
            }

            // Tên location check-out
            if (record.getCheckOutLocationId() != null) {
                workLocationRepository.findById(record.getCheckOutLocationId())
                        .ifPresent(loc -> builder.checkOutLocationName(loc.getName()));
            }
        } else {
            // Chưa có record → chưa check-in
            builder.status(AttendanceStatus.NOT_CHECKED);
        }

        return builder.build();
    }

    // ============================================
    // GET HISTORY
    // ============================================

    @Override
    public List<AttendanceRecordDTO> getHistory(
            Integer userId,
            LocalDate startDate,
            LocalDate endDate) {

        log.info("[MY-ATTENDANCE] Getting history for userId={}, {} to {}",
                userId, startDate, endDate);

        // Validate date range
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before or equal to endDate");
        }
        if (Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays() > 365) {
            throw new IllegalArgumentException("Date range must not exceed 365 days");
        }

        Employee employee = getEmployeeByUserId(userId);

        List<AttendanceRecord> records = attendanceRecordRepository
                .findByEmployeeIdAndDateBetweenWithShift(employee.getId(), startDate, endDate);

        return records.stream()
                .map(this::toAttendanceRecordDTO)
                .collect(Collectors.toList());
    }

    // ============================================
    // GET MONTHLY CALENDAR
    // ============================================

    @Override
    public MyMonthlyCalendarDTO getMonthlyCalendar(Integer userId, int year, int month) {
        log.info("[MY-ATTENDANCE] Getting monthly calendar for userId={}, year={}, month={}",
                userId, year, month);

        // Validate
        validateYearMonth(year, month);

        Employee employee = getEmployeeByUserId(userId);
        Shift shift = getEmployeeShift(employee);

        // Lấy tất cả attendance records trong tháng
        List<AttendanceRecord> records = attendanceRecordRepository
                .findByEmployeeIdAndYearMonth(employee.getId(), year, month);

        // Map date → record để lookup nhanh O(1)
        Map<LocalDate, AttendanceRecord> recordMap = records.stream()
                .collect(Collectors.toMap(AttendanceRecord::getDate, Function.identity()));

        // Parse workDays từ shift
        List<Integer> workDays = parseWorkDays(shift);

        // Build danh sách ngày trong tháng
        YearMonth yearMonth = YearMonth.of(year, month);
        int totalDays = yearMonth.lengthOfMonth();
        List<DayRecordDTO> days = new ArrayList<>();

        for (int dayNum = 1; dayNum <= totalDays; dayNum++) {
            LocalDate date = LocalDate.of(year, month, dayNum);
            AttendanceRecord record = recordMap.get(date);

            DayRecordDTO dayRecord = buildDayRecord(date, record, shift, workDays);
            days.add(dayRecord);
        }

        // Build summary
        MyAttendanceSummaryDTO summary = buildSummary(
                employee.getId(), year, month, workDays, records, yearMonth
        );

        // Build shift info strings
        String shiftStartTime = shift != null ? shift.getStartTime().toString() : null;
        String shiftEndTime = shift != null ? shift.getEndTime().toString() : null;
        String shiftName = shift != null ? shift.getName() : null;
        String shiftCode = shift != null ? shift.getCode() : null;

        String monthName = Month.of(month)
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        return MyMonthlyCalendarDTO.builder()
                .year(year)
                .month(month)
                .monthName(monthName)
                .totalDaysInMonth(totalDays)
                .currentShiftName(shiftName)
                .currentShiftCode(shiftCode)
                .shiftStartTime(shiftStartTime)
                .shiftEndTime(shiftEndTime)
                .workDays(workDays)
                .days(days)
                .summary(summary)
                .build();
    }

    // ============================================
    // GET MONTHLY SUMMARY
    // ============================================

    @Override
    public MyAttendanceSummaryDTO getMonthlySummary(Integer userId, int year, int month) {
        log.info("[MY-ATTENDANCE] Getting monthly summary for userId={}, year={}, month={}",
                userId, year, month);

        validateYearMonth(year, month);

        Employee employee = getEmployeeByUserId(userId);
        Shift shift = getEmployeeShift(employee);
        List<Integer> workDays = parseWorkDays(shift);

        YearMonth yearMonth = YearMonth.of(year, month);
        List<AttendanceRecord> records = attendanceRecordRepository
                .findByEmployeeIdAndYearMonth(employee.getId(), year, month);

        return buildSummary(employee.getId(), year, month, workDays, records, yearMonth);
    }

    // ============================================
    // PRIVATE HELPER METHODS
    // ============================================

    /**
     * Lấy Employee từ userId (JWT)
     * userId → Employee (throw nếu không tìm thấy)
     */
    private Employee getEmployeeByUserId(Integer userId) {
        return employeeRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found for userId: " + userId
                ));
    }

    /**
     * Lấy Shift của Employee
     * Employee có @ManyToOne shift → lazy load
     * → Load đầy đủ từ repository để tránh LazyInitializationException
     */
    private Shift getEmployeeShift(Employee employee) {
        if (employee.getShift() == null) {
            log.warn("[MY-ATTENDANCE] Employee id={} has no shift assigned", employee.getId());
            return null;
        }
        // Load lại từ DB để tránh lazy init exception
        return shiftRepository.findByIdAndIsDeletedFalse(employee.getShift().getId())
                .orElse(null);
    }

    /**
     * Parse workDays từ JSON string của Shift
     * Ví dụ: "[1,2,3,4,5]" → List[1,2,3,4,5]
     */
    private List<Integer> parseWorkDays(Shift shift) {
        if (shift == null || shift.getWorkDays() == null) {
            return List.of(1, 2, 3, 4, 5); // default Mon-Fri
        }
        try {
            return objectMapper.readValue(shift.getWorkDays(), new TypeReference<List<Integer>>() {});
        } catch (Exception e) {
            log.error("[MY-ATTENDANCE] Failed to parse workDays: {}", shift.getWorkDays());
            return List.of(1, 2, 3, 4, 5);
        }
    }

    /**
     * Kiểm tra ngày có phải ngày làm việc không
     * LocalDate.getDayOfWeek().getValue(): 1=Mon ... 7=Sun
     */
    private boolean isWorkDay(LocalDate date, List<Integer> workDays) {
        int dayOfWeek = date.getDayOfWeek().getValue(); // 1=Mon, 7=Sun
        return workDays.contains(dayOfWeek);
    }

    /**
     * Build DayRecordDTO cho 1 ngày
     */
    private DayRecordDTO buildDayRecord(
            LocalDate date,
            AttendanceRecord record,
            Shift shift,
            List<Integer> workDays) {

        boolean isWorkDay = isWorkDay(date, workDays);
        String dayOfWeek = date.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        DayRecordDTO.DayRecordDTOBuilder builder = DayRecordDTO.builder()
                .date(date)
                .dayOfWeek(dayOfWeek)
                .isWorkDay(isWorkDay);

        // Thêm shift info
        if (shift != null) {
            builder.shiftName(shift.getName())
                   .shiftStartTime(shift.getStartTime().toString())
                   .shiftEndTime(shift.getEndTime().toString());
        }

        // Không có attendance record
        if (record == null) {
            // Ngày làm việc mà không có record → ABSENT (nếu ngày đã qua)
            if (isWorkDay && date.isBefore(LocalDate.now())) {
                builder.status(AttendanceStatus.ABSENT);
            } else if (!isWorkDay) {
                // Cuối tuần / ngày nghỉ
                builder.status(null);
            }
            return builder.build();
        }

        // Có attendance record
        builder.attendanceId(record.getId())
               .status(record.getStatus())
               .checkInTime(record.getCheckInTime())
               .checkOutTime(record.getCheckOutTime())
               .workHours(record.getWorkHours() != null
                       ? record.getWorkHours().doubleValue() : null)
               .overtimeHours(record.getOvertimeHours() != null
                       ? record.getOvertimeHours().doubleValue() : null)
               .lateMinutes(record.getLateMinutes())
               .earlyLeaveMinutes(record.getEarlyLeaveMinutes())
               .isVerified(record.getIsVerified())
               .isApproved(record.getIsApproved());

        return builder.build();
    }

    /**
     * Build summary statistics cho tháng
     */
    private MyAttendanceSummaryDTO buildSummary(
            Integer employeeId,
            int year,
            int month,
            List<Integer> workDays,
            List<AttendanceRecord> records,
            YearMonth yearMonth) {

        // ====== Tính tổng ngày làm việc trong tháng ======
        int totalWorkDays = 0;
        LocalDate today = LocalDate.now();

        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);
            // Chỉ đếm ngày làm việc đã qua (kể cả hôm nay)
            if (isWorkDay(date, workDays) && !date.isAfter(today)) {
                totalWorkDays++;
            }
        }

        // ====== Đếm theo status từ records ======
        int presentDays = 0;
        int lateDays = 0;
        int earlyLeaveDays = 0;
        int leaveDays = 0;
        int notCheckedDays = 0;

        for (AttendanceRecord record : records) {
            if (record.getStatus() == null) continue;
            switch (record.getStatus()) {
                case PRESENT   -> presentDays++;
                case LATE      -> { presentDays++; lateDays++; }
                case EARLY_LEAVE -> { presentDays++; earlyLeaveDays++; }
                case LEAVE     -> leaveDays++;
                case NOT_CHECKED -> notCheckedDays++;
                case ABSENT    -> {} // đếm riêng bên dưới
            }
        }

        // Absent = ngày làm việc đã qua - số record có (trừ LEAVE)
        int daysWithRecord = (int) records.stream()
                .filter(r -> r.getStatus() != AttendanceStatus.NOT_CHECKED)
                .count();
        int absentDays = Math.max(0, totalWorkDays - daysWithRecord - leaveDays);

        // ====== Tính giờ từ DB (aggregate queries) ======
        Double totalWorkHours = attendanceRecordRepository
                .sumWorkHoursByEmployeeIdAndYearMonth(employeeId, year, month);
        Double totalOvertimeHours = attendanceRecordRepository
                .sumOvertimeHoursByEmployeeIdAndYearMonth(employeeId, year, month);
        Long totalLateMinutesLong = attendanceRecordRepository
                .sumLateMinutesByEmployeeIdAndYearMonth(employeeId, year, month);
        Long totalEarlyLeaveMinutesLong = attendanceRecordRepository
                .sumEarlyLeaveMinutesByEmployeeIdAndYearMonth(employeeId, year, month);

        double totalLateMinutes = totalLateMinutesLong != null ? totalLateMinutesLong.doubleValue() : 0.0;
        double totalEarlyLeaveMinutes = totalEarlyLeaveMinutesLong != null
                ? totalEarlyLeaveMinutesLong.doubleValue() : 0.0;

        // ====== Tính tỷ lệ ======
        double attendanceRate = totalWorkDays > 0
                ? Math.round((double) presentDays / totalWorkDays * 100 * 10) / 10.0
                : 0.0;

        double punctualityRate = presentDays > 0
                ? Math.round((double) (presentDays - lateDays) / presentDays * 100 * 10) / 10.0
                : 0.0;

        // ====== Kiểm tra hôm nay ======
        boolean hasCheckedInToday = false;
        boolean hasCheckedOutToday = false;

        if (year == today.getYear() && month == today.getMonthValue()) {
            Optional<AttendanceRecord> todayRecord = attendanceRecordRepository
                    .findByEmployeeIdAndDate(employeeId, today);
            if (todayRecord.isPresent()) {
                hasCheckedInToday = todayRecord.get().getCheckInTime() != null;
                hasCheckedOutToday = todayRecord.get().getCheckOutTime() != null;
            }
        }

        return MyAttendanceSummaryDTO.builder()
                .year(year)
                .month(month)
                .totalWorkDays(totalWorkDays)
                .presentDays(presentDays)
                .absentDays(absentDays)
                .lateDays(lateDays)
                .earlyLeaveDays(earlyLeaveDays)
                .leaveDays(leaveDays)
                .notCheckedDays(notCheckedDays)
                .totalWorkHours(totalWorkHours != null ? Math.round(totalWorkHours * 100.0) / 100.0 : 0.0)
                .totalOvertimeHours(totalOvertimeHours != null ? Math.round(totalOvertimeHours * 100.0) / 100.0 : 0.0)
                .totalLateMinutes(totalLateMinutes)
                .totalEarlyLeaveMinutes(totalEarlyLeaveMinutes)
                .attendanceRate(attendanceRate)
                .punctualityRate(punctualityRate)
                .hasCheckedInToday(hasCheckedInToday)
                .hasCheckedOutToday(hasCheckedOutToday)
                .build();
    }

    /**
     * Validate year/month hợp lệ
     */
    private void validateYearMonth(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year < 2000 || year > LocalDate.now().getYear() + 1) {
            throw new IllegalArgumentException("Year is out of valid range");
        }
    }

    /**
     * Convert AttendanceRecord entity → AttendanceRecordDTO
     * Dùng lại DTO đã có trong project
     */
    private AttendanceRecordDTO toAttendanceRecordDTO(AttendanceRecord record) {
        return AttendanceRecordDTO.builder()
                .id(record.getId())
                .employeeId(record.getEmployeeId().longValue())
                .date(record.getDate())
                .shiftId(record.getShiftId())
                .shiftName(record.getShift() != null ? record.getShift().getName() : null)
                .checkInTime(record.getCheckInTime())
                .checkOutTime(record.getCheckOutTime())
                .checkInPhotoUrl(record.getCheckInPhotoUrl())
                .checkOutPhotoUrl(record.getCheckOutPhotoUrl())
                .checkInFaceMatchScore(record.getCheckInFaceMatchScore() != null
                        ? record.getCheckInFaceMatchScore().doubleValue() : null)
                .checkOutFaceMatchScore(record.getCheckOutFaceMatchScore() != null
                        ? record.getCheckOutFaceMatchScore().doubleValue() : null)
                .status(record.getStatus())
                .workHours(record.getWorkHours() != null
                        ? record.getWorkHours().doubleValue() : null)
                .overtimeHours(record.getOvertimeHours() != null
                        ? record.getOvertimeHours().doubleValue() : null)
                .lateMinutes(record.getLateMinutes())
                .earlyLeaveMinutes(record.getEarlyLeaveMinutes())
                .isVerified(record.getIsVerified())
                .isApproved(record.getIsApproved())
                .build();
    }
}