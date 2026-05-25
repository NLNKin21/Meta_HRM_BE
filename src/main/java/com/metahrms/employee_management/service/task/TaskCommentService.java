package com.metahrms.employee_management.service.task;

import com.metahrms.employee_management.dto.request.task.comment.CommentCreateRequest;
import com.metahrms.employee_management.dto.response.task.comment.TaskCommentResponse;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.Task.Task;
import com.metahrms.employee_management.entity.Task.TaskComment;
import com.metahrms.employee_management.entity.User; 
import com.metahrms.employee_management.exception.BusinessException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.exception.TaskException;
import com.metahrms.employee_management.mapper.task.TaskCommentMapper;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.UserRepository;
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
    private final UserRepository userRepository;       // ✅ thêm
    private final TaskCommentMapper commentMapper;
    private final TaskHistoryService historyService;
    private final NotificationService notificationService;

    /**
     * Lấy tất cả comments của task
     * currentUserId = users.id lấy từ JWT
     */
    @Transactional(readOnly = true)
    public List<TaskCommentResponse> getCommentsByTaskId(Integer taskId, Integer currentUserId) {
        log.info("Getting comments for task: {}", taskId);

        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId)
            .stream()
            .map(comment -> {
                boolean isOwner = comment.getUser() != null
                        && comment.getUser().getId().equals(currentUserId);

                TaskCommentResponse response = commentMapper.toResponse(comment, currentUserId);
                response.setCanEdit(isOwner);
                response.setCanDelete(isOwner);
                return response;
            })
            .collect(Collectors.toList());
    }

    /**
     * Thêm comment vào task
     * userId = users.id lấy từ JWT
     */
    @Transactional
    public TaskCommentResponse addComment(Integer taskId, CommentCreateRequest request, Integer userId) {
        log.info("Adding comment to task: {} by userId: {}", taskId, userId);

        // Validate task
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> TaskException.taskNotFound(taskId));

        // ✅ Sửa: lấy User entity từ userRepository thay vì Employee
        User author = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // ✅ Sửa: truyền User entity vào builder
        TaskComment comment = TaskComment.builder()
            .task(task)
            .user(author)  
            .content(request.getContent())
            .attachmentUrl(request.getAttachmentUrl())
            .attachmentName(request.getAttachmentName())
            .attachmentType(request.getAttachmentType())
            .isDeleted(false)
            .build();

        TaskComment saved = commentRepository.save(comment);
        log.info("Added comment ID: {} to task: {}", saved.getId(), taskId);

        // ✅ Lấy Employee để log history và notify (cần employeeId)
        Employee employee = employeeRepository.findByUserId(userId).orElse(null);
        Integer employeeId = employee != null ? employee.getId() : null;

        // Log history
        historyService.logCommentAdded(task, employeeId, request.getContent());

        // Notify task owner and assignee
        notificationService.sendCommentNotification(task, employee, request.getContent());

        TaskCommentResponse response = commentMapper.toResponse(saved, userId);
        response.setCanEdit(true);
        response.setCanDelete(true);

        return response;
    }

    /**
     * Cập nhật comment
     * userId = users.id lấy từ JWT
     */
    @Transactional
    public TaskCommentResponse updateComment(Integer commentId, String content, Integer userId) {
        log.info("Updating comment ID: {} by userId: {}", commentId, userId);

        TaskComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("TaskComment", "id", commentId));

        // ✅ comment.getUser() là User entity
        // comment.getUser().getId() = users.id
        // userId = users.id từ JWT
        // => so sánh đúng
        if (comment.getUser() == null || !comment.getUser().getId().equals(userId)) {
            throw new BusinessException("You can only edit your own comments");
        }

        comment.setContent(content);
        TaskComment saved = commentRepository.save(comment);
        log.info("Updated comment ID: {}", commentId);

        TaskCommentResponse response = commentMapper.toResponse(saved, userId);
        response.setCanEdit(true);
        response.setCanDelete(true);

        return response;
    }

    /**
     * Xóa comment (soft delete)
     * userId = users.id lấy từ JWT
     */
    @Transactional
    public void deleteComment(Integer commentId, Integer userId) {
        log.info("Deleting comment ID: {} by userId: {}", commentId, userId);

        TaskComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("TaskComment", "id", commentId));

        // ✅ So sánh đúng: cả 2 đều là users.id
        if (comment.getUser() == null || !comment.getUser().getId().equals(userId)) {
            throw new BusinessException("You can only delete your own comments");
        }

        comment.setIsDeleted(true);
        commentRepository.save(comment);

        // ✅ Lấy employeeId để log history
        Employee employee = employeeRepository.findByUserId(userId).orElse(null);
        Integer employeeId = employee != null ? employee.getId() : null;

        historyService.logCommentDeleted(comment.getTask(), employeeId);
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