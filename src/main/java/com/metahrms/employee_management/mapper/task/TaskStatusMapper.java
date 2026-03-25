package com.metahrms.employee_management.mapper.task;


import org.springframework.stereotype.Component;

import com.metahrms.employee_management.dto.response.task.task.TaskStatusResponse;
import com.metahrms.employee_management.entity.Task.TaskStatus;

@Component
public class TaskStatusMapper {

    public TaskStatusResponse toResponse(TaskStatus taskStatus) {
        return TaskStatusResponse.builder()
            .id(taskStatus.getId())
            .statusName(taskStatus.getStatusName())
            .statusNameEn(taskStatus.getStatusNameEn())
            .orderIndex(taskStatus.getOrderIndex())
            .color(taskStatus.getColor())
            .icon(taskStatus.getIcon())
            .isCompleted(taskStatus.getIsCompleted())
            .isDefault(taskStatus.getIsDefault())
            .department(taskStatus.getDepartment() != null ? taskStatus.getDepartment().getDeptName() : null)
            .departmentId(taskStatus.getDepartment() != null ? taskStatus.getDepartment().getId() : null)
            .isActive(taskStatus.getIsActive())
            .build();
    }
}