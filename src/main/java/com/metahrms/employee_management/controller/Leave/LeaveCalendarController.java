package com.metahrms.employee_management.controller.Leave;

import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.Leave.LeaveCalendarItemDto;
import com.metahrms.employee_management.service.Leave.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/leave-calendar")
@RequiredArgsConstructor
public class LeaveCalendarController {

    private final LeaveRequestService leaveRequestService;

    @GetMapping
    public ApiResponse<List<LeaveCalendarItemDto>> getCalendar(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        return ApiResponse.success(
                leaveRequestService.getCalendar(startDate, endDate),
                "Lấy lịch nghỉ thành công"
        );
    }
}