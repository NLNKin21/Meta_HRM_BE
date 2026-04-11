package com.metahrms.employee_management.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.metahrms.employee_management.dto.request.Attendance.AttendanceRecordDTO;
import com.metahrms.employee_management.dto.request.Attendance.AttendanceStatusDTO;
import com.metahrms.employee_management.dto.request.Attendance.CheckInRequestDTO;
import com.metahrms.employee_management.dto.request.Attendance.CheckOutRequestDTO;
import com.metahrms.employee_management.dto.request.common.ApiResponse;
import com.metahrms.employee_management.dto.response.Attendance.CheckInResponseDTO;
import com.metahrms.employee_management.dto.response.Attendance.CheckOutResponseDTO;
import com.metahrms.employee_management.entity.Attendance.AttendanceRecord;
import com.metahrms.employee_management.service.AttendanceService;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller cho Attendance Management
 * 
 * Endpoints:
 * - POST /api/attendance/check-in - Check-in với face recognition
 * - POST /api/attendance/check-out - Check-out với face recognition
 * - GET /api/attendance/today - Lấy attendance hôm nay
 * - GET /api/attendance/history - Lấy attendance history
 * - GET /api/attendance/{id} - Lấy attendance by ID
 * - GET /api/attendance/employee/{employeeId} - Lấy attendance của employee
 */
@Slf4j
@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance Management", description = "APIs for employee attendance tracking with face recognition")
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    /**
     * Check-in với face recognition
     */
    @PostMapping("/check-in")
    @Operation(
        summary = "Check-in with face recognition",
        description = """
            Employee check-in with face verification and GPS location validation.
            
            Process:
            1. Verify face using AI service
            2. Validate GPS location within allowed radius
            3. Upload photo to Cloudinary
            4. Calculate late minutes
            5. Create attendance record
            6. Detect anomalies
            
            Returns:
            - Success response with attendance details
            - Warnings if applicable (late, GPS invalid...)
            - Anomalies detected
            """
    )
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<ApiResponse<CheckInResponseDTO.CheckInDataDTO>> checkIn(
            @Valid @RequestBody CheckInRequestDTO request
    ) {
        
        log.info("[CHECK-IN] Received check-in request from employee_id={}", request.getEmployeeId());
        
        try {
            CheckInResponseDTO response = attendanceService.checkIn(request);
            
            if (response.getSuccess()) {
                return ResponseEntity.ok(
                    ApiResponse.success(response.getData(), response.getMessage())
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(response.getMessage()));
            }
            
        } catch (Exception e) {
            log.error("[CHECK-IN] Check-in failed for employee_id={}", request.getEmployeeId(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Check-in failed: " + e.getMessage()));
        }
    }
    
    /**
     * Check-out với face recognition
     */
    @PostMapping("/check-out")
    @Operation(
        summary = "Check-out with face recognition",
        description = """
            Employee check-out with face verification and work hours calculation.
            
            Process:
            1. Verify face using AI service
            2. Validate GPS location
            3. Upload photo to Cloudinary
            4. Calculate work hours and overtime
            5. Update attendance record
            6. Detect anomalies
            
            Returns:
            - Success response with work summary
            - Warnings if applicable (early leave...)
            - Total work hours and overtime
            """
    )
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<ApiResponse<CheckOutResponseDTO.CheckOutDataDTO>> checkOut(
            @Valid @RequestBody CheckOutRequestDTO request
    ) {
        
        log.info("[CHECK-OUT] Received check-out request from employee_id={}", request.getEmployeeId());
        
        try {
            CheckOutResponseDTO response = attendanceService.checkOut(request);
            
            if (response.getSuccess()) {
                return ResponseEntity.ok(
                    ApiResponse.success(response.getData(), response.getMessage())
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(response.getMessage()));
            }
            
        } catch (Exception e) {
            log.error("[CHECK-OUT] Check-out failed for employee_id={}", request.getEmployeeId(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Check-out failed: " + e.getMessage()));
        }
    }
    
    /**
     * Lấy attendance hôm nay của employee
     */
    @GetMapping("/today")
    @Operation(
        summary = "Get today's attendance",
        description = "Get attendance record for current day"
    )
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<ApiResponse<AttendanceRecordDTO>> getTodayAttendance(
            @Parameter(description = "Employee ID", required = true)
            @RequestParam("employeeId") Long employeeId
    ) {
        
        log.info("[ATTENDANCE-TODAY] Getting today's attendance for employee_id={}", employeeId);
        
        LocalDate today = LocalDate.now();
        AttendanceRecord record = attendanceService.getTodayAttendance(employeeId, today);
        
        if (record == null) {
            return ResponseEntity.ok(
                ApiResponse.success(null, "No attendance record for today")
            );
        }
        
        return ResponseEntity.ok(
            ApiResponse.success(convertToDTO(record), "Today's attendance retrieved")
        );
    }
    
    /**
     * Lấy attendance history
     */
    @GetMapping("/history")
    @Operation(
        summary = "Get attendance history",
        description = "Get attendance records for a date range"
    )
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<AttendanceRecordDTO>>> getAttendanceHistory(
            @Parameter(description = "Employee ID", required = true)
            @RequestParam("employeeId") Long employeeId,
            
            @Parameter(description = "Start date (yyyy-MM-dd)")
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date (yyyy-MM-dd)")
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        
        log.info("[ATTENDANCE-HISTORY] Getting history for employee_id={}, range={} to {}", 
            employeeId, startDate, endDate);
        
        List<AttendanceRecord> records = attendanceService.getAttendanceHistory(
            employeeId,
            startDate,
            endDate
        );
        
        List<AttendanceRecordDTO> dtos = records.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success(dtos, "Retrieved " + dtos.size() + " record(s)")
        );
    }
    
    /**
     * Lấy attendance by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get attendance by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<ApiResponse<AttendanceRecordDTO>> getAttendanceById(
            @PathVariable("id") Integer id
    ) {
        
        // TODO: Implement getById in service
        return ResponseEntity.ok(
            ApiResponse.success(null, "Not implemented yet")
        );
    }
    
    /**
     * Lấy tất cả attendance của employee trong tháng
     */
    @GetMapping("/employee/{employeeId}/month")
    @Operation(
        summary = "Get monthly attendance",
        description = "Get all attendance records for a specific month"
    )
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<AttendanceRecordDTO>>> getMonthlyAttendance(
            @PathVariable("employeeId") Long employeeId,
            
            @Parameter(description = "Year (e.g., 2024)")
            @RequestParam("year") int year,
            
            @Parameter(description = "Month (1-12)")
            @RequestParam("month") int month
    ) {
        
        log.info("[ATTENDANCE-MONTH] Getting attendance for employee_id={}, year={}, month={}", 
            employeeId, year, month);
        
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        List<AttendanceRecord> records = attendanceService.getAttendanceHistory(
            employeeId,
            startDate,
            endDate
        );
        
        List<AttendanceRecordDTO> dtos = records.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success(dtos, "Retrieved " + dtos.size() + " record(s)")
        );
    }
    
    /**
     * Kiểm tra status check-in hôm nay
     */
    @GetMapping("/status/today")
    @Operation(
        summary = "Check today's check-in/out status",
        description = "Check if employee has checked in/out today"
    )
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<ApiResponse<AttendanceStatusDTO>> getTodayStatus(
            @RequestParam("employeeId") Long employeeId
    ) {
        
        LocalDate today = LocalDate.now();
        AttendanceRecord record = attendanceService.getTodayAttendance(employeeId, today);
        
        AttendanceStatusDTO status = AttendanceStatusDTO.builder()
            .hasCheckedIn(record != null && record.getCheckInTime() != null)
            .hasCheckedOut(record != null && record.getCheckOutTime() != null)
            .checkInTime(record != null ? record.getCheckInTime() : null)
            .checkOutTime(record != null ? record.getCheckOutTime() : null)
            .status(record != null ? record.getStatus() : null)
            .build();
        
        return ResponseEntity.ok(
            ApiResponse.success(status, "Status retrieved")
        );
    }
    
    // ============================================
    // HELPER METHODS
    // ============================================
    
    /**
     * Convert Entity to DTO
     */
    private AttendanceRecordDTO convertToDTO(AttendanceRecord record) {
        return AttendanceRecordDTO.builder()
            .id(record.getId())
            .employeeId(record.getEmployeeId().longValue()) 
            .date(record.getDate())
            .shiftId(record.getShiftId())
            .shiftName(null)
            .checkInTime(record.getCheckInTime())
            .checkOutTime(record.getCheckOutTime())
            .checkInPhotoUrl(record.getCheckInPhotoUrl())
            .checkOutPhotoUrl(record.getCheckOutPhotoUrl())
            .checkInFaceMatchScore(record.getCheckInFaceMatchScore() != null ? 
                record.getCheckInFaceMatchScore().doubleValue() : null)
            .checkOutFaceMatchScore(record.getCheckOutFaceMatchScore() != null ? 
                record.getCheckOutFaceMatchScore().doubleValue() : null)
            .status(record.getStatus())
            .workHours(record.getWorkHours() != null ? record.getWorkHours().doubleValue() : null)
            .overtimeHours(record.getOvertimeHours() != null ? record.getOvertimeHours().doubleValue() : null)
            .lateMinutes(record.getLateMinutes())
            .earlyLeaveMinutes(record.getEarlyLeaveMinutes())
            .isVerified(record.getIsVerified())
            .isApproved(record.getIsApproved())
            .build();
    }
}