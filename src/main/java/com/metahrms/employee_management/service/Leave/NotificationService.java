package com.metahrms.employee_management.service.Leave;

public interface NotificationService {
    void notifyManager(Integer managerId, String message);
    void notifyHr(Integer hrId, String message);
    void notifyEmployee(Integer employeeId, String message);
}