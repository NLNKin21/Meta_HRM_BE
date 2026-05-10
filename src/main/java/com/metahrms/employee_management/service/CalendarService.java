package com.metahrms.employee_management.service;

import com.metahrms.employee_management.dto.response.CalendarEventResponse;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.Leave.Holiday;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.HolidayRepository;
import com.metahrms.employee_management.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final EmployeeRepository employeeRepository;
    private final HolidayRepository holidayRepository;

    public List<CalendarEventResponse> getMyEvents(int month, int year) {
        Integer userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("Không tìm thấy thông tin người dùng");
        }

        // Khoảng ngày của tháng
        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

        List<CalendarEventResponse> events = new ArrayList<>();

        // ── 1. Sinh nhật ─────────────────────────────────
        Employee employee = employeeRepository.findByUserId(userId+1).orElse(null);

        if (employee != null && employee.getDob() != null) {
            LocalDate dob = employee.getDob();
            LocalDate birthdayThisYear = dob.withYear(year);

            // Chỉ thêm nếu sinh nhật nằm trong tháng đang xem
            if (birthdayThisYear.getMonthValue() == month) {
                int age = Period.between(dob, birthdayThisYear).getYears();

                events.add(CalendarEventResponse.builder()
                        .id("birthday_" + employee.getId())
                        .type("BIRTHDAY")
                        .title("🎂 Sinh nhật của bạn")
                        .date(birthdayThisYear)
                        .description("Chúc mừng sinh nhật lần thứ " + age + "! 🎉")
                        .color("#e91e63")
                        .build());
            }
        }

        // ── 2. Ngày lễ ──────────────────────────────────
        List<Holiday> holidays = holidayRepository.findActiveHolidaysInRange(from, to);

        for (Holiday holiday : holidays) {
            events.add(CalendarEventResponse.builder()
                    .id("holiday_" + holiday.getId())
                    .type("HOLIDAY")
                    .title("🏖️ " + holiday.getName())
                    .date(holiday.getHolidayDate())
                    .description("Ngày nghỉ lễ: " + holiday.getName())
                    .color("#ff9800")
                    .build());
        }

        // ── 3. Sắp xếp theo ngày ────────────────────────
        events.sort(Comparator.comparing(CalendarEventResponse::getDate));

        return events;
    }
}