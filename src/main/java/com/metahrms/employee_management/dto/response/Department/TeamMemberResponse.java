package com.metahrms.employee_management.dto.response.Department;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberResponse {
    private Integer id;
    private String fullName;
    private String email;
    private String avatar;
    private String position;
    private String roleInDept;  // HEAD, DEPUTY, STAFF
    private String status;      // ACTIVE, INACTIVE
    
    // Task statistics
    private Integer taskCount;
    private Integer completedTasks;
    private Integer inProgressTasks;
    private Integer overdueTasks;
    
    // Additional info
    private String phoneNumber;
    private String gender;
    private String hireDate;
}