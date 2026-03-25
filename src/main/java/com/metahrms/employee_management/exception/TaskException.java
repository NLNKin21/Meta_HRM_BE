package com.metahrms.employee_management.exception;

public class TaskException extends BusinessException {

    public TaskException(String message) {
        super(message);
    }

    public TaskException(String errorCode, String message) {
        super(errorCode, message);
    }

    // ========== STATIC FACTORY METHODS ==========

    public static TaskException taskNotFound(Integer taskId) {
        return new TaskException("TASK_NOT_FOUND", 
            String.format("Task with ID %d not found", taskId));
    }

    public static TaskException taskCodeNotFound(String taskCode) {
        return new TaskException("TASK_NOT_FOUND", 
            String.format("Task with code %s not found", taskCode));
    }

    public static TaskException duplicateTaskCode(String taskCode) {
        return new TaskException("DUPLICATE_TASK_CODE", 
            String.format("Task code %s already exists", taskCode));
    }

    public static TaskException invalidStatusTransition(String fromStatus, String toStatus) {
        return new TaskException("INVALID_STATUS_TRANSITION", 
            String.format("Cannot transition from '%s' to '%s'", fromStatus, toStatus));
    }

    public static TaskException taskAlreadyCompleted(String taskCode) {
        return new TaskException("TASK_ALREADY_COMPLETED", 
            String.format("Task %s is already completed", taskCode));
    }

    public static TaskException invalidDueDate() {
        return new TaskException("INVALID_DUE_DATE", 
            "Due date cannot be before start date");
    }

    public static TaskException noPermission(String action) {
        return new TaskException("NO_PERMISSION", 
            String.format("You don't have permission to %s this task", action));
    }
}