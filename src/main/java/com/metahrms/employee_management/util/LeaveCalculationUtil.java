package com.metahrms.employee_management.util;

import com.metahrms.employee_management.enums.Leave.LeaveDurationType;
import com.metahrms.employee_management.enums.Leave.LeaveUnit;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

public class LeaveCalculationUtil {

    private LeaveCalculationUtil() {
    }

    public static BigDecimal calculateLeaveDays(
            LocalDate startDate,
            LocalDate endDate,
            LeaveUnit leaveUnit,
            LeaveDurationType startSession,
            LeaveDurationType endSession,
            Set<LocalDate> holidays
    ) {
        validateInput(startDate, endDate, leaveUnit, startSession, endSession);

        if (leaveUnit == LeaveUnit.HALF_DAY) {
            return calculateHalfDay(startDate, endDate, startSession, endSession, holidays);
        }

        return calculateFullDayRange(startDate, endDate, startSession, endSession, holidays);
    }

    private static void validateInput(
            LocalDate startDate,
            LocalDate endDate,
            LeaveUnit leaveUnit,
            LeaveDurationType startSession,
            LeaveDurationType endSession
    ) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được để trống");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được lớn hơn ngày kết thúc");
        }
        if (leaveUnit == null || startSession == null || endSession == null) {
            throw new IllegalArgumentException("Thông tin thời lượng nghỉ không hợp lệ");
        }
    }

    private static BigDecimal calculateHalfDay(
            LocalDate startDate,
            LocalDate endDate,
            LeaveDurationType startSession,
            LeaveDurationType endSession,
            Set<LocalDate> holidays
    ) {
        if (!startDate.equals(endDate)) {
            throw new IllegalArgumentException("Nghỉ nửa ngày chỉ áp dụng trong 1 ngày");
        }
        if (isNonWorkingDay(startDate, holidays)) {
            throw new IllegalArgumentException("Không thể nghỉ vào ngày lễ hoặc cuối tuần");
        }
        if (startSession == LeaveDurationType.FULL_DAY || endSession == LeaveDurationType.FULL_DAY) {
            throw new IllegalArgumentException("Nghỉ nửa ngày không được chọn FULL_DAY");
        }
        if (startSession != endSession) {
            throw new IllegalArgumentException("Nghỉ nửa ngày phải chọn cùng một buổi");
        }
        return BigDecimal.valueOf(0.5);
    }

    private static BigDecimal calculateFullDayRange(
            LocalDate startDate,
            LocalDate endDate,
            LeaveDurationType startSession,
            LeaveDurationType endSession,
            Set<LocalDate> holidays
    ) {
        BigDecimal workingDays = BigDecimal.ZERO;
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            if (!isNonWorkingDay(current, holidays)) {
                workingDays = workingDays.add(BigDecimal.ONE);
            }
            current = current.plusDays(1);
        }

        if (workingDays.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Khoảng thời gian không có ngày làm việc hợp lệ");
        }

        if (startDate.equals(endDate)) {
            if (startSession == LeaveDurationType.FULL_DAY && endSession == LeaveDurationType.FULL_DAY) {
                return BigDecimal.ONE;
            }
            if (startSession == endSession && startSession != LeaveDurationType.FULL_DAY) {
                return BigDecimal.valueOf(0.5);
            }
            throw new IllegalArgumentException("Cấu hình buổi nghỉ không hợp lệ");
        }

        BigDecimal total = workingDays;

        if (startSession == LeaveDurationType.AFTERNOON && !isNonWorkingDay(startDate, holidays)) {
            total = total.subtract(BigDecimal.valueOf(0.5));
        } else if (startSession != LeaveDurationType.FULL_DAY) {
            throw new IllegalArgumentException("Ngày bắt đầu chỉ được chọn FULL_DAY hoặc AFTERNOON");
        }

        if (endSession == LeaveDurationType.MORNING && !isNonWorkingDay(endDate, holidays)) {
            total = total.subtract(BigDecimal.valueOf(0.5));
        } else if (endSession != LeaveDurationType.FULL_DAY) {
            throw new IllegalArgumentException("Ngày kết thúc chỉ được chọn FULL_DAY hoặc MORNING");
        }

        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tổng số ngày nghỉ không hợp lệ");
        }

        return total;
    }

    private static boolean isNonWorkingDay(LocalDate date, Set<LocalDate> holidays) {
        DayOfWeek d = date.getDayOfWeek();
        return d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY || holidays.contains(date);
    }
}