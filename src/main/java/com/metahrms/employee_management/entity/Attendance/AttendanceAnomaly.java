package com.metahrms.employee_management.entity.Attendance;

import java.time.LocalDateTime;

import com.metahrms.employee_management.entity.BaseEntity;
import com.metahrms.employee_management.enums.Attendance.AnomalySeverity;
import com.metahrms.employee_management.enums.Attendance.AnomalyType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "attendance_anomalies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AttendanceAnomaly extends BaseEntity {

    // ✅ Relationship chính - sử dụng Entity thay vì Integer ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    private AttendanceRecord attendance;

    @Enumerated(EnumType.STRING)
    @Column(name = "anomaly_type", nullable = false)
    private AnomalyType anomalyType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private AnomalySeverity severity = AnomalySeverity.MEDIUM;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @Column(name = "resolved")
    private Boolean resolved = false;

    @Column(name = "resolved_by")
    private Integer resolvedBy;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolution_note", columnDefinition = "TEXT")
    private String resolutionNote;

    /**
     * Lấy attendance ID (tiện dụng khi cần ID mà không load entity)
     */
    public Integer getAttendanceId() {
        return attendance != null ? attendance.getId() : null;
    }
    
    /**
     * Kiểm tra đã resolve chưa
     */
    public boolean isResolved() {
        return Boolean.TRUE.equals(resolved);
    }
    
    /**
     * Resolve anomaly
     */
    public void resolve(Integer resolvedByUserId, String note) {
        this.resolved = true;
        this.resolvedBy = resolvedByUserId;
        this.resolvedAt = LocalDateTime.now();
        this.resolutionNote = note;
    }
}