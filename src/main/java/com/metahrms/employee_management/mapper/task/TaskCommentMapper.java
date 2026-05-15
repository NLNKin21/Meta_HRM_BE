package com.metahrms.employee_management.mapper.task;


import org.springframework.stereotype.Component;

import com.metahrms.employee_management.dto.response.task.comment.TaskCommentResponse;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.Task.TaskComment;
import com.metahrms.employee_management.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TaskCommentMapper {

    private final EmployeeRepository employeeRepository;

    public TaskCommentResponse toResponse(TaskComment comment, Integer currentUserId) {
        
      
        Integer userId = comment.getUser() != null ? comment.getUser().getId() : null;

        
        Employee employee = null;
        if (userId != null) {
            employee = employeeRepository.findByUserId(userId).orElse(null);
        }

        String userName = employee != null 
            ? employee.getFullName() 
            : (comment.getUser() != null ? comment.getUser().getUsername() : null);

        String userEmail = comment.getUser() != null 
            ? comment.getUser().getEmail() 
            : null;

        return TaskCommentResponse.builder()
            .id(comment.getId())
            .taskId(comment.getTask() != null ? comment.getTask().getId() : null)
            .userId(userId)
            .userName(userName)
            .userEmail(userEmail)
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .canEdit(userId != null && userId.equals(currentUserId))
            .canDelete(userId != null && userId.equals(currentUserId))
            .build();
    }


    public TaskCommentResponse toResponse(TaskComment comment) {
        return toResponse(comment, null);
    }
}