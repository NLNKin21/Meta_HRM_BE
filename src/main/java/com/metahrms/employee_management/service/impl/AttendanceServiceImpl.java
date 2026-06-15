package com.metahrms.employee_management.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metahrms.employee_management.dto.request.Attendance.CheckInRequestDTO;
import com.metahrms.employee_management.dto.request.Attendance.CheckOutRequestDTO;
import com.metahrms.employee_management.dto.response.Attendance.CheckInResponseDTO;
import com.metahrms.employee_management.dto.response.Attendance.CheckOutResponseDTO;
import com.metahrms.employee_management.dto.response.face.FaceVerifyResponseDTO;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.Attendance.AttendanceAnomaly;
import com.metahrms.employee_management.entity.Attendance.AttendanceRecord;
import com.metahrms.employee_management.entity.Attendance.Shift;
import com.metahrms.employee_management.entity.Attendance.WorkLocation;
import com.metahrms.employee_management.enums.Attendance.AnomalySeverity;
import com.metahrms.employee_management.enums.Attendance.AnomalyType;
import com.metahrms.employee_management.enums.Attendance.AttendanceStatus;
import com.metahrms.employee_management.exception.BusinessException;
import com.metahrms.employee_management.exception.FaceRecognitionException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.Attendance.AttendanceAnomalyRepository;
import com.metahrms.employee_management.repository.Attendance.AttendanceRecordRepository;
import com.metahrms.employee_management.repository.Attendance.ShiftRepository;
import com.metahrms.employee_management.repository.Attendance.WorkLocationRepository;
import com.metahrms.employee_management.service.AttendanceService;
import com.metahrms.employee_management.service.CloudinaryService;
import com.metahrms.employee_management.service.EmployeeFaceService;
import com.metahrms.employee_management.service.FaceRecognitionClient;
import com.metahrms.employee_management.util.ImageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation của AttendanceService với Face Recognition
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceAnomalyRepository attendanceAnomalyRepository;
    private final EmployeeRepository employeeRepository;
    private final ShiftRepository shiftRepository;
    private final WorkLocationRepository workLocationRepository;
    private final EmployeeFaceService employeeFaceService;
    private final FaceRecognitionClient faceRecognitionClient;
    private final ObjectMapper objectMapper;
    private final CloudinaryService cloudinaryService;
    
    // Constants
    private static final double GPS_ACCURACY_METERS = 100.0; // Bán kính cho phép
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    @Override
    @Transactional
    public CheckInResponseDTO checkIn(CheckInRequestDTO request) {
        
        log.info("[CHECK-IN] Starting check-in for employee_id={}", request.getEmployeeId());
        
        // 1. Validate employee exists
        Employee employee = employeeRepository.findById(request.getEmployeeId().intValue())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Employee not found: " + request.getEmployeeId()
            ));
        
        // 2. Check if already checked in today
        LocalDate today = LocalDate.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        AttendanceRecord existingRecord = attendanceRecordRepository
            .findByEmployeeIdAndDate(employee.getId(), today)
            .orElse(null);
        
        if (existingRecord != null && existingRecord.getCheckInTime() != null) {
            throw new BusinessException(
                "Already checked in today at " + existingRecord.getCheckInTime()
            );
        }
        
        // 3. Verify GPS location
        WorkLocation location = workLocationRepository.findById(request.getLocationId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Work location not found: " + request.getLocationId()
            ));
        
        boolean gpsValid = verifyGPSLocation(
            request.getLatitude(),
            request.getLongitude(),
            request.getLocationId()
        );
        
        List<String> warnings = new ArrayList<>();
        List<CheckInResponseDTO.AnomalyInfo> anomalies = new ArrayList<>();
        
        if (!gpsValid) {
            warnings.add("GPS location is outside allowed radius");
            anomalies.add(CheckInResponseDTO.AnomalyInfo.builder()
                .type("GPS_INVALID")
                .severity("HIGH")
                .message("Check-in location is outside work location radius")
                .build()
            );
        }
        
        // 4. Verify face recognition
        List<List<Double>> knownEmbeddings = employeeFaceService.getEmployeeEmbeddings(
            request.getEmployeeId()
        );
        
        String processedImage;
        try {
            processedImage = ImageUtil.resizeAndCompress(request.getFaceImageBase64());
        } catch (Exception e) {
            throw new FaceRecognitionException("Failed to process face image", e);
        }
        
        FaceVerifyResponseDTO faceResult = faceRecognitionClient.verifyFace(
            request.getEmployeeId(),
            processedImage,
            knownEmbeddings,
            null // Use default threshold
        );
        
        if (!faceResult.getSuccess()) {
            throw new FaceRecognitionException("Face verification failed: " + faceResult.getMessage());
        }
        
        // 5. Check face match
        if (!faceResult.getIsMatch()) {
            anomalies.add(CheckInResponseDTO.AnomalyInfo.builder()
                .type("FACE_MISMATCH")
                .severity("CRITICAL")
                .message("Face does not match registered faces")
                .build()
            );
            
            throw new BusinessException(
                String.format("Face verification failed. Confidence: %.2f%%", faceResult.getConfidence())
            );
        }
        
        // 6. Upload photo to Cloudinary
        String photoUrl = uploadCheckInPhoto(request.getFaceImageBase64(), request.getEmployeeId());
        
        // 7. Get shift information
Shift shift = null;
if (employee.getShift() != null) {
    shift = shiftRepository.findById(employee.getShift().getId()).orElse(null);
}
if (shift == null) {
    throw new BusinessException("Employee has no assigned shift");
}
        
        // 8. Calculate late minutes
        LocalDateTime checkInTime = LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        int lateMinutes = calculateLateMinutes(checkInTime.toLocalTime(), shift.getStartTime());
        
        AttendanceStatus status = determineCheckInStatus(lateMinutes, shift);
        
        if (lateMinutes > 0) {
            warnings.add(String.format("Late by %d minutes", lateMinutes));
        }
        
        // 9. Create or update attendance record
        AttendanceRecord record;
        if (existingRecord != null) {
            record = existingRecord;
        } else {
            record = new AttendanceRecord();
            record.setEmployeeId(employee.getId());
            record.setDate(today);
            record.setShiftId(shift.getId());
        }
        
        record.setCheckInTime(checkInTime);
        record.setCheckInLocationId(location.getId());
        record.setCheckInLat(BigDecimal.valueOf(request.getLatitude()));
        record.setCheckInLng(BigDecimal.valueOf(request.getLongitude()));
        record.setCheckInPhotoUrl(photoUrl);
        record.setCheckInFaceMatchScore(BigDecimal.valueOf(faceResult.getConfidence()));
        record.setCheckInDeviceInfo(request.getDeviceInfo());
        record.setCheckInNote(request.getNote());
        record.setStatus(status);
        record.setLateMinutes(lateMinutes);
        record.setIsVerified(faceResult.getConfidence() >= 95.0);
        
        AttendanceRecord savedRecord = attendanceRecordRepository.save(record);
        
        log.info("[CHECK-IN] Success: attendance_id={}, status={}, late_minutes={}, confidence={}",
            savedRecord.getId(),
            status,
            lateMinutes,
            faceResult.getConfidence()
        );
        
        // 10. Handle anomalies
        handleCheckInAnomalies(savedRecord, faceResult, gpsValid);
        
        // 11. Build response
        return CheckInResponseDTO.builder()
            .success(true)
            .message("Check-in successful")
            .data(CheckInResponseDTO.CheckInDataDTO.builder()
                .attendanceId(savedRecord.getId())
                .employeeId(request.getEmployeeId())
                .checkInTime(checkInTime)
                .status(status)
                .lateMinutes(lateMinutes)
                .faceMatchScore(faceResult.getConfidence())
                .checkInPhotoUrl(photoUrl)
                .isVerified(savedRecord.getIsVerified())
                .warnings(warnings)
                .anomalies(anomalies)
                .build())
            .build();
    }
    
    @Override
    @Transactional
    public CheckOutResponseDTO checkOut(CheckOutRequestDTO request) {
        
        log.info("[CHECK-OUT] Starting check-out for employee_id={}", request.getEmployeeId());
        
        // 1. Validate employee
        Employee employee = employeeRepository.findById(request.getEmployeeId().intValue())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Employee not found: " + request.getEmployeeId()
            ));
        
        // 2. Get today's attendance record
        LocalDate today = LocalDate.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        AttendanceRecord record = attendanceRecordRepository
            .findByEmployeeIdAndDate(employee.getId(), today)
            .orElseThrow(() -> new BusinessException(
                "No check-in record found for today. Please check-in first."
            ));
        
        // 3. Check if already checked out
        if (record.getCheckOutTime() != null) {
            throw new BusinessException(
                "Already checked out today at " + record.getCheckOutTime()
            );
        }
        
        // 4. Verify GPS
        WorkLocation location = workLocationRepository.findById(request.getLocationId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Work location not found: " + request.getLocationId()
            ));
        
        boolean gpsValid = verifyGPSLocation(
            request.getLatitude(),
            request.getLongitude(),
            request.getLocationId()
        );
        
        List<String> warnings = new ArrayList<>();
        List<CheckInResponseDTO.AnomalyInfo> anomalies = new ArrayList<>();
        
        if (!gpsValid) {
            warnings.add("GPS location is outside allowed radius");
            anomalies.add(CheckInResponseDTO.AnomalyInfo.builder()
                .type("GPS_INVALID")
                .severity("MEDIUM")
                .message("Check-out location is outside work location radius")
                .build()
            );
        }
        
        // 5. Verify face
        List<List<Double>> knownEmbeddings = employeeFaceService.getEmployeeEmbeddings(
            request.getEmployeeId()
        );
        
        String processedImage;
        try {
            processedImage = ImageUtil.resizeAndCompress(request.getFaceImageBase64());
        } catch (Exception e) {
            throw new FaceRecognitionException("Failed to process face image", e);
        }
        
        FaceVerifyResponseDTO faceResult = faceRecognitionClient.verifyFace(
            request.getEmployeeId(),
            processedImage,
            knownEmbeddings,
            null
        );
        
        if (!faceResult.getSuccess()) {
            throw new FaceRecognitionException("Face verification failed: " + faceResult.getMessage());
        }
        
        if (!faceResult.getIsMatch()) {
            anomalies.add(CheckInResponseDTO.AnomalyInfo.builder()
                .type("FACE_MISMATCH")
                .severity("CRITICAL")
                .message("Face does not match registered faces")
                .build()
            );
            
            throw new BusinessException(
                String.format("Face verification failed. Confidence: %.2f%%", faceResult.getConfidence())
            );
        }
        
        // 6. Upload photo
        String photoUrl = uploadCheckOutPhoto(request.getFaceImageBase64(), request.getEmployeeId());
        
        // 7. Calculate work hours and early leave
LocalDateTime checkOutTime = LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));

// Load shift từ DB (tránh null do dual mapping)
Shift shift = null;
if (record.getShiftId() != null) {
    shift = shiftRepository.findById(record.getShiftId()).orElse(null);
}
if (shift == null && employee.getShift() != null) {
    shift = shiftRepository.findById(employee.getShift().getId()).orElse(null);
}

int earlyLeaveMinutes = 0;
if (shift != null) {
    earlyLeaveMinutes = calculateEarlyLeaveMinutes(
        checkOutTime.toLocalTime(),
        shift.getEndTime()
    );
} else {
    log.warn("[CHECK-OUT] No shift found for employee_id={}, skipping early leave calculation",
        request.getEmployeeId());
}
        
        // 8. Update record
        record.setCheckOutTime(checkOutTime);
        record.setCheckOutLocationId(location.getId());
        record.setCheckOutLat(BigDecimal.valueOf(request.getLatitude()));
        record.setCheckOutLng(BigDecimal.valueOf(request.getLongitude()));
        record.setCheckOutPhotoUrl(photoUrl);
        record.setCheckOutFaceMatchScore(BigDecimal.valueOf(faceResult.getConfidence()));
        record.setCheckOutDeviceInfo(request.getDeviceInfo());
        record.setCheckOutNote(request.getNote());
        record.setEarlyLeaveMinutes(earlyLeaveMinutes);
        
        // 9. Calculate work hours
        calculateWorkHours(record);
        
        // 10. Determine final status
        AttendanceStatus finalStatus = determineFinalStatus(record);
        record.setStatus(finalStatus);
        
        AttendanceRecord savedRecord = attendanceRecordRepository.save(record);
        
        log.info("[CHECK-OUT] Success: attendance_id={}, work_hours={}, overtime={}, status={}",
            savedRecord.getId(),
            savedRecord.getWorkHours(),
            savedRecord.getOvertimeHours(),
            finalStatus
        );
        
        // 11. Handle anomalies
        handleCheckOutAnomalies(savedRecord, faceResult, gpsValid);
        
        // 12. Build response
        return CheckOutResponseDTO.builder()
            .success(true)
            .message("Check-out successful")
            .data(CheckOutResponseDTO.CheckOutDataDTO.builder()
                .attendanceId(savedRecord.getId())
                .employeeId(request.getEmployeeId())
                .checkInTime(record.getCheckInTime())
                .checkOutTime(checkOutTime)
                .status(finalStatus)
                .workHours(savedRecord.getWorkHours())
                .overtimeHours(savedRecord.getOvertimeHours())
                .earlyLeaveMinutes(earlyLeaveMinutes)
                .faceMatchScore(faceResult.getConfidence())
                .checkOutPhotoUrl(photoUrl)
                .isVerified(faceResult.getConfidence() >= 95.0)
                .warnings(warnings)
                .anomalies(anomalies)
                .build())
            .build();
    }
    
    @Override
    public AttendanceRecord getTodayAttendance(Long employeeId, LocalDate date) {
        return attendanceRecordRepository
            .findByEmployeeIdAndDate(employeeId.intValue(), date)
            .orElse(null);
    }
    
    @Override
    public List<AttendanceRecord> getAttendanceHistory(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate) {
        
        return attendanceRecordRepository
            .findByEmployeeIdAndDateBetween(
                employeeId.intValue(),
                startDate,
                endDate
            );
    }
    
    @Override
public void calculateWorkHours(AttendanceRecord record) {
    if (record.getCheckInTime() == null || record.getCheckOutTime() == null) {
        record.setWorkHours(BigDecimal.ZERO);
        record.setOvertimeHours(BigDecimal.ZERO);
        return;
    }

    Duration duration = Duration.between(record.getCheckInTime(), record.getCheckOutTime());
    long totalMinutes = duration.toMinutes();

    // Load shift từ DB
    long breakMinutes = 60; // default
    if (record.getShiftId() != null) {
        Shift shift = shiftRepository.findById(record.getShiftId()).orElse(null);
        if (shift != null && shift.getBreakDuration() != null) {
            breakMinutes = shift.getBreakDuration();
        }
    }

    long workMinutes = totalMinutes - breakMinutes;
    if (workMinutes < 0) workMinutes = 0;

    BigDecimal workHours = BigDecimal.valueOf(workMinutes)
        .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

    record.setWorkHours(workHours);

    BigDecimal standardHours = BigDecimal.valueOf(8);
    if (workHours.compareTo(standardHours) > 0) {
        record.setOvertimeHours(workHours.subtract(standardHours));
    } else {
        record.setOvertimeHours(BigDecimal.ZERO);
    }
}
    
    @Override
    public boolean verifyGPSLocation(Double lat, Double lng, Integer locationId) {
        
        WorkLocation location = workLocationRepository.findById(locationId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Location not found: " + locationId
            ));
        
        double distance = calculateDistance(
            lat,
            lng,
            location.getLatitude().doubleValue(),
            location.getLongitude().doubleValue()
        );
        
        double allowedRadius = location.getRadius() != null ? 
            location.getRadius() : 
            GPS_ACCURACY_METERS;
        
        boolean isValid = distance <= allowedRadius;
        
        log.debug("GPS verification: distance={}m, allowed={}m, valid={}",
            distance,
            allowedRadius,
            isValid
        );
        
        return isValid;
    }
    
    // ============================================
    // PRIVATE HELPER METHODS
    // ============================================
    
    /**
     * Calculate distance between two GPS coordinates (Haversine formula)
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c * 1000; // Convert to meters
    }
    
    /**
     * Calculate late minutes
     */
    private int calculateLateMinutes(LocalTime checkInTime, LocalTime shiftStartTime) {
        
        if (checkInTime.isBefore(shiftStartTime) || checkInTime.equals(shiftStartTime)) {
            return 0;
        }
        
        Duration duration = Duration.between(shiftStartTime, checkInTime);
        return (int) duration.toMinutes();
    }
    
    /**
     * Calculate early leave minutes
     */
    private int calculateEarlyLeaveMinutes(LocalTime checkOutTime, LocalTime shiftEndTime) {
        
        if (checkOutTime.isAfter(shiftEndTime) || checkOutTime.equals(shiftEndTime)) {
            return 0;
        }
        
        Duration duration = Duration.between(checkOutTime, shiftEndTime);
        return (int) duration.toMinutes();
    }
    
    /**
     * Determine check-in status
     */
    private AttendanceStatus determineCheckInStatus(int lateMinutes, Shift shift) {
        
        if (lateMinutes == 0) {
            return AttendanceStatus.PRESENT;
        }
        
        int threshold = shift.getLateThreshold() != null ? shift.getLateThreshold() : 15;
        
        if (lateMinutes <= threshold) {
            return AttendanceStatus.PRESENT;
        } else {
            return AttendanceStatus.LATE;
        }
    }
    
    /**
     * Determine final status after check-out
     */
    private AttendanceStatus determineFinalStatus(AttendanceRecord record) {
    if (record.getLateMinutes() > 0 && record.getEarlyLeaveMinutes() > 0) {
        return AttendanceStatus.LATE;
    }
    if (record.getEarlyLeaveMinutes() > 0) {
        return AttendanceStatus.EARLY_LEAVE;
    }
    if (record.getLateMinutes() > 0) {
        return AttendanceStatus.LATE;
    }
    return AttendanceStatus.PRESENT;
}
    
    /**
     * Handle check-in anomalies
     */
    private void handleCheckInAnomalies(
            AttendanceRecord record,
            FaceVerifyResponseDTO faceResult,
            boolean gpsValid) {
        
        List<AttendanceAnomaly> anomalies = new ArrayList<>();
        
        // GPS anomaly
        if (!gpsValid) {
            anomalies.add(AttendanceAnomaly.builder()
                .attendance(record)
                .anomalyType(AnomalyType.GPS_INVALID)
                .severity(AnomalySeverity.HIGH)
                .description("Check-in location is outside allowed radius")
                .resolved(false)
                .build()
            );
        }
        
        // Low confidence anomaly
        if (faceResult.getConfidence() < 90.0) {
            anomalies.add(AttendanceAnomaly.builder()
                .attendance(record)
                .anomalyType(AnomalyType.SUSPICIOUS)
                .severity(AnomalySeverity.MEDIUM)
                .description(String.format("Low face match confidence: %.2f%%", faceResult.getConfidence()))
                .resolved(false)
                .build()
            );
        }
        
        // Save anomalies
        if (!anomalies.isEmpty()) {
            attendanceAnomalyRepository.saveAll(anomalies);
            log.warn("Created {} anomalies for attendance_id={}", anomalies.size(), record.getId());
        }
    }
    
    /**
     * Handle check-out anomalies
     */
    private void handleCheckOutAnomalies(
            AttendanceRecord record,
            FaceVerifyResponseDTO faceResult,
            boolean gpsValid) {
        
        List<AttendanceAnomaly> anomalies = new ArrayList<>();
        
        if (!gpsValid) {
            anomalies.add(AttendanceAnomaly.builder()
                .attendance(record)
                .anomalyType(AnomalyType.GPS_INVALID)
                .severity(AnomalySeverity.MEDIUM)
                .description("Check-out location is outside allowed radius")
                .resolved(false)
                .build()
            );
        }
        
        if (faceResult.getConfidence() < 90.0) {
            anomalies.add(AttendanceAnomaly.builder()
                .attendance(record)
                .anomalyType(AnomalyType.SUSPICIOUS)
                .severity(AnomalySeverity.MEDIUM)
                .description(String.format("Low face match confidence at check-out: %.2f%%", faceResult.getConfidence()))
                .resolved(false)
                .build()
            );
        }
        
        if (!anomalies.isEmpty()) {
            attendanceAnomalyRepository.saveAll(anomalies);
        }
    }
    
    /**
     * Upload check-in photo to Cloudinary
     */
    private String uploadCheckInPhoto(String base64Image, Long employeeId) {
        try {
            return cloudinaryService.uploadCheckInPhoto(base64Image, employeeId);
        } catch (Exception e) {
            log.error("Failed to upload check-in photo", e);
            return null;
        }
    }

    private String uploadCheckOutPhoto(String base64Image, Long employeeId) {
        try {
            return cloudinaryService.uploadCheckOutPhoto(base64Image, employeeId);
        } catch (Exception e) {
            log.error("Failed to upload check-out photo", e);
            return null;
        }
    }
}