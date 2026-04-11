package com.metahrms.employee_management.entity.Attendance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.metahrms.employee_management.entity.BaseEntity;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.enums.Attendance.AttendanceStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attendance_records", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AttendanceRecord extends BaseEntity {

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "shift_id")
    private Integer shiftId;

    // ============ CHECK-IN DATA ============
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_in_location_id")
    private Integer checkInLocationId;

    @Column(name = "check_in_lat", precision = 10, scale = 8)
    private BigDecimal checkInLat;

    @Column(name = "check_in_lng", precision = 11, scale = 8)
    private BigDecimal checkInLng;

    @Column(name = "check_in_photo_url", length = 500)
    private String checkInPhotoUrl;

    @Column(name = "check_in_face_match_score", precision = 5, scale = 2)
    private BigDecimal checkInFaceMatchScore;

    @Column(name = "check_in_device_info", columnDefinition = "JSON")
    private String checkInDeviceInfo;

    @Column(name = "check_in_note", columnDefinition = "TEXT")
    private String checkInNote;

    // ============ CHECK-OUT DATA ============
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "check_out_location_id")
    private Integer checkOutLocationId;

    @Column(name = "check_out_lat", precision = 10, scale = 8)
    private BigDecimal checkOutLat;

    @Column(name = "check_out_lng", precision = 11, scale = 8)
    private BigDecimal checkOutLng;

    @Column(name = "check_out_photo_url", length = 500)
    private String checkOutPhotoUrl;

    @Column(name = "check_out_face_match_score", precision = 5, scale = 2)
    private BigDecimal checkOutFaceMatchScore;

    @Column(name = "check_out_device_info", columnDefinition = "JSON")
    private String checkOutDeviceInfo;

    @Column(name = "check_out_note", columnDefinition = "TEXT")
    private String checkOutNote;

    // ============ CALCULATED FIELDS ============
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AttendanceStatus status = AttendanceStatus.NOT_CHECKED;

    @Column(name = "work_hours", precision = 4, scale = 2)
    private BigDecimal workHours = BigDecimal.ZERO;

    @Column(name = "overtime_hours", precision = 4, scale = 2)
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @Column(name = "late_minutes")
    private Integer lateMinutes = 0;

    @Column(name = "early_leave_minutes")
    private Integer earlyLeaveMinutes = 0;

    // ============ VERIFICATION ============
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "verified_by")
    private Integer verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verification_note", columnDefinition = "TEXT")
    private String verificationNote;

    // ============ APPROVAL ============
    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "approved_by")
    private Integer approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approval_note", columnDefinition = "TEXT")
    private String approvalNote;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    // ⭐ Relationships
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", insertable = false, updatable = false)
    private Shift shift;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_in_location_id", insertable = false, updatable = false)
    private WorkLocation checkInLocation;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_out_location_id", insertable = false, updatable = false)
    private WorkLocation checkOutLocation;
}