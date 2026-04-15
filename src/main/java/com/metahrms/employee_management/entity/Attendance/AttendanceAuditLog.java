package com.metahrms.employee_management.entity.Attendance;

import com.metahrms.employee_management.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "attendance_audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AttendanceAuditLog extends BaseEntity {

    @Column(name = "attendance_id", nullable = false)
    private Integer attendanceId;

    /**
     * EDIT, APPROVE, REJECT
     */
    @Column(name = "action", nullable = false, length = 20)
    private String action;

    /**
     * Giá trị trước khi thay đổi (JSON)
     * Ví dụ: {"checkInTime":"08:30","status":"LATE"}
     */
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    /**
     * Giá trị sau khi thay đổi (JSON)
     */
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    /**
     * Lý do chỉnh sửa/từ chối (bắt buộc với EDIT và REJECT)
     */
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    /**
     * userId của người thực hiện (từ JWT)
     */
    @Column(name = "performed_by", nullable = false)
    private Integer performedBy;

    /**
     * Tên người thực hiện (lưu snapshot tránh join)
     */
    @Column(name = "performed_by_name", length = 150)
    private String performedByName;
}