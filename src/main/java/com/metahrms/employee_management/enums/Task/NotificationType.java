package com.metahrms.employee_management.enums.Task;

public enum NotificationType {
    TASK_ASSIGNED,          // Được giao task
    TASK_UPDATED,           // Task được cập nhật
    TASK_COMMENTED,         // Có comment mới
    TASK_STATUS_CHANGED,    // Đổi trạng thái
    DEADLINE_WARNING,       // Cảnh báo deadline
    TASK_OVERDUE,          // Quá hạn
    TASK_COMPLETED,        // Hoàn thành
    MENTION                // Được tag trong comment
}