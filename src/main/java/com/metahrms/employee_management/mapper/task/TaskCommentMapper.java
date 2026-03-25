package com.metahrms.employee_management.mapper.task;


import org.springframework.stereotype.Component;

import com.metahrms.employee_management.dto.response.task.comment.TaskCommentResponse;
import com.metahrms.employee_management.entity.Task.TaskComment;

@Component
public class TaskCommentMapper {

    public TaskCommentResponse toResponse(TaskComment comment) {
        return TaskCommentResponse.builder()
            .id(comment.getId())
            .taskId(comment.getTask() != null ? comment.getTask().getId() : null)
            .userId(comment.getUser() != null ? comment.getUser().getId() : null)
            .userName(comment.getUser() != null ? comment.getUser().getFullName() : null)
            .userEmail(comment.getUser() != null ? comment.getUser().getPhoneNumber() : null) // Adjust
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .build();
    }
}