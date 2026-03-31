package com.metahrms.employee_management.service.Leave;

import com.metahrms.employee_management.dto.request.Leave.*;
import com.metahrms.employee_management.dto.response.Leave.LeaveCalendarItemDto;
import com.metahrms.employee_management.dto.response.Leave.LeaveRequestResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestService {
    LeaveRequestResponseDto createDraft(LeaveRequestCreateDto dto);
    LeaveRequestResponseDto updateDraft(Long id, LeaveRequestUpdateDraftDto dto);
    LeaveRequestResponseDto submit(Long id, LeaveSubmitDto dto);
    LeaveRequestResponseDto cancel(Long id, LeaveCancelDto dto);
    LeaveRequestResponseDto getById(Long id);
    List<LeaveRequestResponseDto> getByEmployee(Integer employeeId);
    List<LeaveCalendarItemDto> getCalendar(LocalDate startDate, LocalDate endDate);
}