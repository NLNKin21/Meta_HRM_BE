package com.metahrms.employee_management.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TaskCodeGenerator {

    private final AtomicInteger counter = new AtomicInteger(0);
    private String lastDate = "";

    /**
     * Generate task code: TSK-YYYYMMDD-XXX
     * Example: TSK-20240115-001
     */
    public synchronized String generateTaskCode() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        if (!today.equals(lastDate)) {
            lastDate = today;
            counter.set(0);
        }
        
        int sequence = counter.incrementAndGet();
        return String.format("TSK-%s-%03d", today, sequence);
    }

    /**
     * Generate project code: PRJ-YYYY-XXX
     * Example: PRJ-2024-001
     */
    public synchronized String generateProjectCode(int existingCount) {
        String year = String.valueOf(LocalDate.now().getYear());
        return String.format("PRJ-%s-%03d", year, existingCount + 1);
    }
}