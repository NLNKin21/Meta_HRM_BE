// package com.metahrms.employee_management.service.impl;

// import com.metahrms.employee_management.dto.response.ApiResponse;
// import com.metahrms.employee_management.entity.Attendance;
// import com.metahrms.employee_management.service.AttendanceService;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import lombok.RequiredArgsConstructor;
// import org.springframework.format.annotation.DateTimeFormat;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.Map;

// @RestController
// @RequestMapping("/attendance")
// @RequiredArgsConstructor
// @Tag(name = "Attendance Management", description = "APIs for managing attendance")
// public class AttendanceController {

//     private final AttendanceService attendanceService;

//     @PostMapping("/check-in")
//     @Operation(summary = "Check in for an employee")
//     public ResponseEntity<ApiResponse<Attendance>> checkIn(
//             @RequestParam Long employeeId) {
//         Attendance attendance = attendanceService.checkIn(employeeId);
//         return ResponseEntity.ok(ApiResponse.success("Checked in successfully", attendance));
//     }

//     @PostMapping("/check-out")
//     @Operation(summary = "Check out for an employee")
//     public ResponseEntity<ApiResponse<Attendance>> checkOut(
//             @RequestParam Long employeeId) {
//         Attendance attendance = attendanceService.checkOut(employeeId);
//         return ResponseEntity.ok(ApiResponse.success("Checked out successfully", attendance));
//     }

//     @GetMapping("/today/{employeeId}")
//     @Operation(summary = "Get today's attendance for an employee")
//     public ResponseEntity<ApiResponse<Attendance>> getTodayAttendance(
//             @PathVariable Long employeeId) {
//         Attendance attendance = attendanceService.getTodayAttendance(employeeId);
//         return ResponseEntity.ok(ApiResponse.success(attendance));
//     }

//     @GetMapping("/employee/{employeeId}")
//     @Operation(summary = "Get attendance history for an employee")
//     public ResponseEntity<ApiResponse<List<Attendance>>> getAttendanceByEmployee(
//             @PathVariable Long employeeId) {
//         List<Attendance> attendances = attendanceService.getAttendanceByEmployee(employeeId);
//         return ResponseEntity.ok(ApiResponse.success(attendances));
//     }

//     @GetMapping("/employee/{employeeId}/range")
//     @Operation(summary = "Get attendance by date range")
//     public ResponseEntity<ApiResponse<List<Attendance>>> getAttendanceByDateRange(
//             @PathVariable Long employeeId,
//             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//         List<Attendance> attendances = attendanceService.getAttendanceByDateRange(employeeId, startDate, endDate);
//         return ResponseEntity.ok(ApiResponse.success(attendances));
//     }

//     @GetMapping("/employee/{employeeId}/summary")
//     @Operation(summary = "Get monthly attendance summary")
//     public ResponseEntity<ApiResponse<Map<String, Object>>> getMonthlyAttendanceSummary(
//             @PathVariable Long employeeId,
//             @RequestParam int month,
//             @RequestParam int year) {
//         Map<String, Object> summary = attendanceService.getMonthlyAttendanceSummary(employeeId, month, year);
//         return ResponseEntity.ok(ApiResponse.success(summary));
//     }
// }
