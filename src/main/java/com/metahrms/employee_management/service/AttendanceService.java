package com.metahrms.employee_management.service;



import java.time.LocalDate;
import java.util.List;

import com.metahrms.employee_management.dto.request.Attendance.CheckInRequestDTO;
import com.metahrms.employee_management.dto.request.Attendance.CheckOutRequestDTO;
import com.metahrms.employee_management.dto.response.Attendance.CheckInResponseDTO;
import com.metahrms.employee_management.dto.response.Attendance.CheckOutResponseDTO;
import com.metahrms.employee_management.entity.Attendance.AttendanceRecord;

/**
 * Service interface cho Attendance operations
 */
public interface AttendanceService {
    
    /**
     * Check-in với face recognition
     * 
     * @param request CheckInRequestDTO chứa ảnh, GPS, device info
     * @return CheckInResponseDTO
     */
    CheckInResponseDTO checkIn(CheckInRequestDTO request);
    
    /**
     * Check-out với face recognition
     */
    CheckOutResponseDTO checkOut(CheckOutRequestDTO request);
    
    /**
     * Lấy attendance record của employee trong ngày
     */
    AttendanceRecord getTodayAttendance(Long employeeId, LocalDate date);
    
    /**
     * Lấy attendance history
     */
    List<AttendanceRecord> getAttendanceHistory(Long employeeId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Tính toán work hours
     */
    void calculateWorkHours(AttendanceRecord record);
    
    /**
     * Verify GPS location
     */
    boolean verifyGPSLocation(Double lat, Double lng, Integer locationId);
}