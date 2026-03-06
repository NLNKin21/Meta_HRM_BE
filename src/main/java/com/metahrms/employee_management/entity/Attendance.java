<<<<<<< HEAD
package com.metahrms.employee_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendances", indexes = {
    @Index(name = "idx_attendance_date", columnList = "attendance_date"),
    @Index(name = "idx_attendance_employee", columnList = "employee_id")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "attendance_date"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "work_hours")
    private Double workHours;

    @Column(name = "overtime_hours")
    private Double overtimeHours = 0.0;

    @Column(name = "late_minutes")
    private Integer lateMinutes = 0;

    @Column(name = "early_leave_minutes")
    private Integer earlyLeaveMinutes = 0;

    @Column(name = "work_shift", length = 20)
    private String workShift = "MORNING";

    @Column(columnDefinition = "TEXT")
    private String notes;

    // ==================== BUSINESS METHODS ====================
    
    @PrePersist
    @PreUpdate
    public void calculateWorkHours() {
        if (checkInTime != null && checkOutTime != null) {
            long minutes = java.time.Duration.between(checkInTime, checkOutTime).toMinutes();
            this.workHours = Math.round(minutes / 60.0 * 100.0) / 100.0;
            
            // Calculate overtime (standard: 8 hours)
            if (this.workHours > 8.0) {
                this.overtimeHours = Math.round((this.workHours - 8.0) * 100.0) / 100.0;
            }
            
            // Calculate late minutes (standard start: 9:00 AM)
            LocalTime standardStart = LocalTime.of(9, 0);
            if (checkInTime.toLocalTime().isAfter(standardStart)) {
                this.lateMinutes = (int) java.time.Duration.between(standardStart, checkInTime.toLocalTime()).toMinutes();
            }
            
            // Calculate early leave (standard end: 17:00)
            LocalTime standardEnd = LocalTime.of(17, 0);
            if (checkOutTime.toLocalTime().isBefore(standardEnd)) {
                this.earlyLeaveMinutes = (int) java.time.Duration.between(checkOutTime.toLocalTime(), standardEnd).toMinutes();
            }
        }
    }
}
=======
// package com.metahrms.employee_management.entity;

// import jakarta.persistence.*;
// import lombok.*;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.LocalTime;

// @Entity
// @Table(name = "attendances", indexes = {
//     @Index(name = "idx_attendance_date", columnList = "attendance_date"),
//     @Index(name = "idx_attendance_employee", columnList = "employee_id")
// }, uniqueConstraints = {
//     @UniqueConstraint(columnNames = {"employee_id", "attendance_date"})
// })
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class Attendance extends BaseEntity {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "employee_id", nullable = false)
//     private Employee employee;

//     @Column(name = "attendance_date", nullable = false)
//     private LocalDate attendanceDate;

//     @Column(name = "check_in_time")
//     private LocalDateTime checkInTime;

//     @Column(name = "check_out_time")
//     private LocalDateTime checkOutTime;

//     @Column(name = "work_hours")
//     private Double workHours;

//     @Column(name = "overtime_hours")
//     private Double overtimeHours = 0.0;

//     @Column(name = "late_minutes")
//     private Integer lateMinutes = 0;

//     @Column(name = "early_leave_minutes")
//     private Integer earlyLeaveMinutes = 0;

//     @Column(name = "work_shift", length = 20)
//     private String workShift = "MORNING";

//     @Column(columnDefinition = "TEXT")
//     private String notes;

//     // ==================== BUSINESS METHODS ====================
    
//     @PrePersist
//     @PreUpdate
//     public void calculateWorkHours() {
//         if (checkInTime != null && checkOutTime != null) {
//             long minutes = java.time.Duration.between(checkInTime, checkOutTime).toMinutes();
//             this.workHours = Math.round(minutes / 60.0 * 100.0) / 100.0;
            
//             // Calculate overtime (standard: 8 hours)
//             if (this.workHours > 8.0) {
//                 this.overtimeHours = Math.round((this.workHours - 8.0) * 100.0) / 100.0;
//             }
            
//             // Calculate late minutes (standard start: 9:00 AM)
//             LocalTime standardStart = LocalTime.of(9, 0);
//             if (checkInTime.toLocalTime().isAfter(standardStart)) {
//                 this.lateMinutes = (int) java.time.Duration.between(standardStart, checkInTime.toLocalTime()).toMinutes();
//             }
            
//             // Calculate early leave (standard end: 17:00)
//             LocalTime standardEnd = LocalTime.of(17, 0);
//             if (checkOutTime.toLocalTime().isBefore(standardEnd)) {
//                 this.earlyLeaveMinutes = (int) java.time.Duration.between(checkOutTime.toLocalTime(), standardEnd).toMinutes();
//             }
//         }
//     }
// }
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
