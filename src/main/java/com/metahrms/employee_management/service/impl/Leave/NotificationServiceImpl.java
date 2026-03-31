package com.metahrms.employee_management.service.impl.Leave;

import com.metahrms.employee_management.service.Leave.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void notifyManager(Integer managerId, String message) {
        log.info("Notify MANAGER {}: {}", managerId, message);
    }

    @Override
    public void notifyHr(Integer hrId, String message) {
        log.info("Notify HR {}: {}", hrId, message);
    }

    @Override
    public void notifyEmployee(Integer employeeId, String message) {
        log.info("Notify EMPLOYEE {}: {}", employeeId, message);
    }
}