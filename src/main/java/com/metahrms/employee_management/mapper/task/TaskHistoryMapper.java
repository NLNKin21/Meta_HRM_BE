package com.metahrms.employee_management.mapper.task;

import org.springframework.stereotype.Component;

import com.metahrms.employee_management.dto.response.task.history.TaskHistoryResponse;
import com.metahrms.employee_management.entity.Task.TaskHistory;

@Component
public class TaskHistoryMapper {

    public TaskHistoryResponse toResponse(TaskHistory history) {
        return TaskHistoryResponse.builder()
            .id(history.getId())
            .taskId(history.getTask() != null ? history.getTask().getId() : null)
            .userId(history.getUser() != null ? history.getUser().getId() : null)
            .userName(history.getUser() != null ? history.getUser().getFullName() : null)
            .fieldName(history.getFieldName())
            .oldValue(history.getOldValue())
            .newValue(history.getNewValue())
            .actionType(history.getActionType())
            .createdAt(history.getCreatedAt())
            .build();
    }
}