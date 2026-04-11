package com.metahrms.employee_management.entity.Attendance;

import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.metahrms.employee_management.entity.BaseEntity;
import com.metahrms.employee_management.entity.Employee;

@Entity
@Table(name = "shifts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Shift extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", unique = true, length = 50)
    private String code;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "late_threshold")
    private Integer lateThreshold = 15; // phút

    @Column(name = "early_leave_threshold")
    private Integer earlyLeaveThreshold = 15; // phút

    @Column(name = "check_in_start_before")
    private Integer checkInStartBefore = 30; // phút

    @Column(name = "check_in_end_after")
    private Integer checkInEndAfter = 120; // phút

    @Column(name = "work_days", columnDefinition = "JSON")
    private String workDays; // JSON: [1,2,3,4,5]

    @Column(name = "break_duration")
    private Integer breakDuration = 60; // phút

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    // ⭐ Relationship với Employee (One-to-Many)
    @OneToMany(mappedBy = "shift", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Employee> employees;

    // ⭐ Helper methods
    public int getTotalWorkMinutes() {
        return (int) java.time.Duration.between(startTime, endTime).toMinutes() - breakDuration;
    }

    public double getTotalWorkHours() {
        return getTotalWorkMinutes() / 60.0;
    }
}