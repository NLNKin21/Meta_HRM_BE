package com.metahrms.employee_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventResponse {
    private String id;
    private String type;       // BIRTHDAY, HOLIDAY
    private String title;
    private LocalDate date;
    private String description;
    private String color;
}