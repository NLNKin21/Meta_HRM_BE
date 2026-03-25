package com.metahrms.employee_management.service.task;


import com.metahrms.employee_management.dto.response.task.history.TaskHistoryResponse;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.Task.Task;
import com.metahrms.employee_management.entity.Task.TaskHistory;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.mapper.task.TaskHistoryMapper;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.Task.TaskHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskHistoryService {

    private final TaskHistoryRepository historyRepository;
    private final EmployeeRepository employeeRepository;
    private final TaskHistoryMapper historyMapper;

    /**
     * Lấy history của task
     */
    @Transactional(readOnly = true)
    public List<TaskHistoryResponse> getHistoryByTaskId(Integer taskId) {
        log.info("Getting history for task: {}", taskId);
        return historyRepository.findByTaskIdOrderByCreatedAtDesc(taskId)
            .stream()
            .map(historyMapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy history theo field
     */
    @Transactional(readOnly = true)
    public List<TaskHistoryResponse> getHistoryByField(Integer taskId, String fieldName) {
        return historyRepository.findByTaskIdAndFieldName(taskId, fieldName)
            .stream()
            .map(historyMapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Log task created
     */
    @Transactional
    public void logTaskCreated(Task task, Integer userId) {
        log.info("Logging task created: {}", task.getTaskCode());
        createHistory(task, userId, "task", null, task.getTaskCode(), "CREATE");
    }

    /**
     * Log task deleted
     */
    @Transactional
    public void logTaskDeleted(Task task, Integer userId) {
        log.info("Logging task deleted: {}", task.getTaskCode());
        createHistory(task, userId, "task", task.getTaskCode(), null, "DELETE");
    }

    /**
     * Log field change
     */
    @Transactional
    public void logFieldChange(Task task, Integer userId, String fieldName, String oldValue, String newValue) {
        log.info("Logging field change - Task: {}, Field: {}, From: {} To: {}", 
            task.getTaskCode(), fieldName, oldValue, newValue);
        createHistory(task, userId, fieldName, oldValue, newValue, "UPDATE");
    }

    /**
     * Log comment added
     */
    @Transactional
    public void logCommentAdded(Task task, Integer userId, String content) {
        log.info("Logging comment added to task: {}", task.getTaskCode());
        String preview = content.length() > 50 ? content.substring(0, 50) + "..." : content;
        createHistory(task, userId, "comment", null, preview, "COMMENT");
    }

    /**
     * Log comment deleted
     */
    @Transactional
    public void logCommentDeleted(Task task, Integer userId) {
        log.info("Logging comment deleted from task: {}", task.getTaskCode());
        createHistory(task, userId, "comment", "Deleted", null, "DELETE_COMMENT");
    }

    // ========== HELPER METHODS ==========

    private void createHistory(Task task, Integer userId, String fieldName, 
                               String oldValue, String newValue, String actionType) {
        Employee user = employeeRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", userId));

        TaskHistory history = TaskHistory.builder()
            .task(task)
            .user(user)
            .fieldName(fieldName)
            .oldValue(oldValue)
            .newValue(newValue)
            .actionType(actionType)
            .createdAt(LocalDateTime.now())
            .build();

        historyRepository.save(history);
    }
}