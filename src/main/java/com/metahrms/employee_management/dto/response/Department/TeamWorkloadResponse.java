package com.metahrms.employee_management.dto.response.Department;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamWorkloadResponse {
    private Integer userId;
    private String fullName;
    private String avatar;
    private String position;
    
    // Workload statistics
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer inProgressTasks;
    private Integer pendingTasks;
    private Integer overdueTasks;
    
    // Completion rate (%)
    private Integer completionRate;
    
    // Average time per task (days)
    private Double avgCompletionTime;
    
    // Workload level: LOW, MEDIUM, HIGH
    private String workloadLevel;
}