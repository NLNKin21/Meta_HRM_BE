package com.metahrms.employee_management.service.task;


import com.metahrms.employee_management.dto.request.task.comment.CommentCreateRequest;
import com.metahrms.employee_management.dto.response.task.comment.TaskCommentResponse;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.Task.Task;
import com.metahrms.employee_management.entity.Task.TaskComment;
import com.metahrms.employee_management.exception.BusinessException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.exception.TaskException;
import com.metahrms.employee_management.mapper.task.TaskCommentMapper;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.Task.TaskCommentRepository;
import com.metahrms.employee_management.repository.Task.TaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskCommentService {

    private final TaskCommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final TaskCommentMapper commentMapper;
    private final TaskHistoryService historyService;
    private final NotificationService notificationService;

    /**
     * Lấy tất cả comments của task
     */
    @Transactional(readOnly = true)
    public List<TaskCommentResponse> getCommentsByTaskId(Integer taskId, Integer currentUserId) {
        log.info("Getting comments for task: {}", taskId);
        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId)
            .stream()
            .map(comment -> {
                TaskCommentResponse response = commentMapper.toResponse(comment);
                response.setCanEdit(comment.getUser().getId().equals(currentUserId));
                response.setCanDelete(comment.getUser().getId().equals(currentUserId));
                return response;
            })
            .collect(Collectors.toList());
    }

    /**
     * Thêm comment vào task
     */
    @Transactional
    public TaskCommentResponse addComment(Integer taskId, CommentCreateRequest request, Integer userId) {
        log.info("Adding comment to task: {} by user: {}", taskId, userId);

        // Validate task
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> TaskException.taskNotFound(taskId));

        // Validate user
        Employee user = employeeRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", userId));

        // Create comment
        TaskComment comment = TaskComment.builder()
            .task(task)
            .user(user)
            .content(request.getContent())
            .isDeleted(false)
            .build();

        TaskComment saved = commentRepository.save(comment);
        log.info("Added comment ID: {} to task: {}", saved.getId(), taskId);

        // Log history
        historyService.logCommentAdded(task, userId, request.getContent());

        // Notify task owner and assignee
        notificationService.sendCommentNotification(task, user, request.getContent());

        TaskCommentResponse response = commentMapper.toResponse(saved);
        response.setCanEdit(true);
        response.setCanDelete(true);

        return response;
    }

    /**
     * Cập nhật comment
     */
    @Transactional
    public TaskCommentResponse updateComment(Integer commentId, String content, Integer userId) {
        log.info("Updating comment ID: {} by user: {}", commentId, userId);

        TaskComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("TaskComment", "id", commentId));

        // Check permission
        if (!comment.getUser().getId().equals(userId)) {
            throw new BusinessException("You can only edit your own comments");
        }

        comment.setContent(content);
        TaskComment saved = commentRepository.save(comment);

        log.info("Updated comment ID: {}", commentId);

        TaskCommentResponse response = commentMapper.toResponse(saved);
        response.setCanEdit(true);
        response.setCanDelete(true);

        return response;
    }

    /**
     * Xóa comment (soft delete)
     */
    @Transactional
    public void deleteComment(Integer commentId, Integer userId) {
        log.info("Deleting comment ID: {} by user: {}", commentId, userId);

        TaskComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("TaskComment", "id", commentId));

        // Check permission
        if (!comment.getUser().getId().equals(userId)) {
            throw new BusinessException("You can only delete your own comments");
        }

        comment.setIsDeleted(true);
        commentRepository.save(comment);

        // Log history
        historyService.logCommentDeleted(comment.getTask(), userId);

        log.info("Deleted comment ID: {}", commentId);
    }

    /**
     * Đếm số comments của task
     */
    @Transactional(readOnly = true)
    public Long countCommentsByTaskId(Integer taskId) {
        return commentRepository.countByTaskId(taskId);
    }
}