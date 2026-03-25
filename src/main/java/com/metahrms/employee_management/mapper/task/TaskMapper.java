package com.metahrms.employee_management.mapper.task;



import org.springframework.stereotype.Component;

import com.metahrms.employee_management.dto.response.task.task.TaskDetailResponse;
import com.metahrms.employee_management.dto.response.task.task.TaskResponse;
import com.metahrms.employee_management.dto.response.task.task.TaskSummaryResponse;
import com.metahrms.employee_management.entity.Task.Task;

@Component
public class TaskMapper {

    /**
     * Convert Task entity to TaskResponse (for list view)
     */
    public TaskResponse toTaskResponse(Task task) {
        return TaskResponse.builder()
            .id(task.getId())
            .taskCode(task.getTaskCode())
            .title(task.getTitle())
            .taskType(task.getTaskType() != null ? task.getTaskType().name() : null)
            .priority(task.getPriority() != null ? task.getPriority().name() : null)
            .statusId(task.getStatus() != null ? task.getStatus().getId() : null)
            .statusName(task.getStatus() != null ? task.getStatus().getStatusName() : null)
            .statusColor(task.getStatus() != null ? task.getStatus().getColor() : null)
            .statusIcon(task.getStatus() != null ? task.getStatus().getIcon() : null)
            .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
            .assigneeName(task.getAssignee() != null ? task.getAssignee().getFullName() : null)
            .assigneeEmail(task.getAssignee() != null ? task.getAssignee().getPhoneNumber() : null) // Adjust based on your Employee entity
            .reporterId(task.getReporter() != null ? task.getReporter().getId() : null)
            .reporterName(task.getReporter() != null ? task.getReporter().getFullName() : null)
            .departmentId(task.getDepartment() != null ? task.getDepartment().getId() : null)
            .departmentName(task.getDepartment() != null ? task.getDepartment().getDeptName() : null)
            .projectId(task.getProject() != null ? task.getProject().getId() : null)
            .projectName(task.getProject() != null ? task.getProject().getProjectName() : null)
            .estimatedHours(task.getEstimatedHours())
            .actualHours(task.getActualHours())
            .startDate(task.getStartDate())
            .dueDate(task.getDueDate())
            .completedAt(task.getCompletedAt())
            .completionRate(task.getCompletionRate())
            .isLate(task.getIsLate())
            .isUrgent(task.getIsUrgent())
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt())
            .build();
    }

    /**
     * Convert Task entity to TaskDetailResponse (for detail view)
     */
    public TaskDetailResponse toTaskDetailResponse(Task task) {
        return TaskDetailResponse.builder()
            .id(task.getId())
            .taskCode(task.getTaskCode())
            .title(task.getTitle())
            .description(task.getDescription())
            .taskType(task.getTaskType() != null ? task.getTaskType().name() : null)
            .priority(task.getPriority() != null ? task.getPriority().name() : null)
            .statusId(task.getStatus() != null ? task.getStatus().getId() : null)
            .statusName(task.getStatus() != null ? task.getStatus().getStatusName() : null)
            .statusColor(task.getStatus() != null ? task.getStatus().getColor() : null)
            .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
            .assigneeName(task.getAssignee() != null ? task.getAssignee().getFullName() : null)
            .reporterId(task.getReporter() != null ? task.getReporter().getId() : null)
            .reporterName(task.getReporter() != null ? task.getReporter().getFullName() : null)
            .approverId(task.getApprover() != null ? task.getApprover().getId() : null)
            .approverName(task.getApprover() != null ? task.getApprover().getFullName() : null)
            .departmentId(task.getDepartment() != null ? task.getDepartment().getId() : null)
            .departmentName(task.getDepartment() != null ? task.getDepartment().getDeptName() : null)
            .projectId(task.getProject() != null ? task.getProject().getId() : null)
            .projectName(task.getProject() != null ? task.getProject().getProjectName() : null)
            .projectCode(task.getProject() != null ? task.getProject().getProjectCode() : null)
            .estimatedHours(task.getEstimatedHours())
            .actualHours(task.getActualHours())
            .startDate(task.getStartDate())
            .dueDate(task.getDueDate())
            .completedAt(task.getCompletedAt())
            .completionRate(task.getCompletionRate())
            .isLate(task.getIsLate())
            .isUrgent(task.getIsUrgent())
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt())
            .build();
    }

    /**
     * Convert Task entity to TaskSummaryResponse (for Board/Kanban)
     */
    public TaskSummaryResponse toTaskSummaryResponse(Task task) {
        return TaskSummaryResponse.builder()
            .id(task.getId())
            .taskCode(task.getTaskCode())
            .title(task.getTitle())
            .priority(task.getPriority() != null ? task.getPriority().name() : null)
            .statusId(task.getStatus() != null ? task.getStatus().getId() : null)
            .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
            .assigneeName(task.getAssignee() != null ? task.getAssignee().getFullName() : null)
            .dueDate(task.getDueDate())
            .isLate(task.getIsLate())
            .isUrgent(task.getIsUrgent())
            .completionRate(task.getCompletionRate())
            .build();
    }
}