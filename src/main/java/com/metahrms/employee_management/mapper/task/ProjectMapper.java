package com.metahrms.employee_management.mapper.task;


import org.springframework.stereotype.Component;

import com.metahrms.employee_management.dto.response.task.project.ProjectResponse;
import com.metahrms.employee_management.entity.Task.Project;

@Component
public class ProjectMapper {

    public ProjectResponse toResponse(Project project) {
        return ProjectResponse.builder()
            .id(project.getId())
            .projectCode(project.getProjectCode())
            .projectName(project.getProjectName())
            .description(project.getDescription())
            .departmentId(project.getDepartment() != null ? project.getDepartment().getId() : null)
            .departmentName(project.getDepartment() != null ? project.getDepartment().getDeptName() : null)
            .managerId(project.getManager() != null ? project.getManager().getId() : null)
            .managerName(project.getManager() != null ? project.getManager().getFullName() : null)
            .startDate(project.getStartDate())
            .endDate(project.getEndDate())
            .status(project.getStatus() != null ? project.getStatus().name() : null)
            .isActive(project.getIsActive())
            .createdAt(project.getCreatedAt())
            .updatedAt(project.getUpdatedAt())
            .build();
    }
}
