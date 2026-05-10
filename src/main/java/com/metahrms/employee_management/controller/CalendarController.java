package com.metahrms.employee_management.controller;

import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.CalendarEventResponse;
import com.metahrms.employee_management.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Calendar", description = "API lịch cá nhân")
@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @Operation(summary = "Lấy sự kiện lịch cá nhân theo tháng")
    @GetMapping("/my-events")
    public ApiResponse<List<CalendarEventResponse>> getMyEvents(
            @RequestParam("month") int month,
            @RequestParam("year") int year
    ) {
        List<CalendarEventResponse> events = calendarService.getMyEvents(month, year);

        return ApiResponse.<List<CalendarEventResponse>>builder()
                .status("success")
                .message("Lấy sự kiện lịch thành công")
                .data(events)
                .build();
    }
}