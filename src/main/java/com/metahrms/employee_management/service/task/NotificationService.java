package com.metahrms.employee_management.service.task;

import com.metahrms.employee_management.dto.response.task.notification.NotificationResponse;
import com.metahrms.employee_management.entity.Task.Notification;
import com.metahrms.employee_management.entity.Task.Task;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.enums.Task.NotificationType;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.mapper.task.NotificationMapper;
import com.metahrms.employee_management.repository.Task.NotificationRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationMapper notificationMapper;

    // ========== QUERY METHODS ==========

    /**
     * Lấy notifications của user (có phân trang)
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsByUser(Integer userId, Pageable pageable) {
        log.info("Getting notifications for user: {}", userId);
        return notificationRepository.findByUserId(userId, pageable)
            .map(notificationMapper::toResponse);
    }

    /**
     * Lấy unread notifications của user
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Integer userId) {
        log.info("Getting unread notifications for user: {}", userId);
        return notificationRepository.findUnreadByUserId(userId)
            .stream()
            .map(notificationMapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Đếm unread notifications
     */
    @Transactional(readOnly = true)
    public Long countUnread(Integer userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    // ========== UPDATE METHODS ==========

    /**
     * Đánh dấu đã đọc
     */
    @Transactional
    public void markAsRead(Integer notificationId) {
        log.info("Marking notification as read: {}", notificationId);
        notificationRepository.markAsRead(notificationId, LocalDateTime.now());
    }

    /**
     * Đánh dấu tất cả đã đọc
     */
    @Transactional
    public void markAllAsRead(Integer userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        notificationRepository.markAllAsReadByUser(userId, LocalDateTime.now());
    }

    // ========== NOTIFICATION SENDING METHODS ==========

    /**
     * Gửi thông báo khi được giao task
     */
    @Transactional
    public void sendTaskAssignedNotification(Task task) {
        log.info("Sending task assigned notification for task: {}", task.getTaskCode());

        Notification notification = Notification.builder()
            .user(task.getAssignee())
            .type(NotificationType.TASK_ASSIGNED)
            .referenceId(task.getId())
            .title("Bạn được giao task mới")
            .message(String.format("Task '%s' (%s) đã được giao cho bạn bởi %s", 
                task.getTitle(), 
                task.getTaskCode(),
                task.getReporter().getFullName()))
            .link("/tasks/" + task.getId())
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();

        notificationRepository.save(notification);
        log.info("Sent task assigned notification to user: {}", task.getAssignee().getId());
    }

    /**
     * Gửi thông báo khi status thay đổi
     */
    @Transactional
    public void sendStatusChangeNotification(Task task, String oldStatus) {
        log.info("Sending status change notification for task: {}", task.getTaskCode());

        // Notify reporter
        if (!task.getAssignee().getId().equals(task.getReporter().getId())) {
            Notification notification = Notification.builder()
                .user(task.getReporter())
                .type(NotificationType.TASK_STATUS_CHANGED)
                .referenceId(task.getId())
                .title("Task đã cập nhật trạng thái")
                .message(String.format("Task '%s' đã chuyển từ '%s' sang '%s'", 
                    task.getTaskCode(),
                    oldStatus, 
                    task.getStatus().getStatusName()))
                .link("/tasks/" + task.getId())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

            notificationRepository.save(notification);
        }
    }

    /**
     * Gửi thông báo khi có comment mới
     */
    @Transactional
    public void sendCommentNotification(Task task, Employee commenter, String content) {
        log.info("Sending comment notification for task: {}", task.getTaskCode());

        String preview = content.length() > 50 ? content.substring(0, 50) + "..." : content;

        // Notify assignee nếu không phải người comment
        if (!task.getAssignee().getId().equals(commenter.getId())) {
            createNotification(
                task.getAssignee(),
                NotificationType.TASK_COMMENTED,
                task.getId(),
                "Có comment mới trong task",
                String.format("%s đã comment: \"%s\"", commenter.getFullName(), preview),
                "/tasks/" + task.getId()
            );
        }

        // Notify reporter nếu không phải người comment và khác assignee
        if (!task.getReporter().getId().equals(commenter.getId()) 
            && !task.getReporter().getId().equals(task.getAssignee().getId())) {
            createNotification(
                task.getReporter(),
                NotificationType.TASK_COMMENTED,
                task.getId(),
                "Có comment mới trong task của bạn",
                String.format("%s đã comment trong task '%s'", 
                    commenter.getFullName(), task.getTaskCode()),
                "/tasks/" + task.getId()
            );
        }
    }

    /**
     * Gửi cảnh báo deadline
     */
    @Transactional
    public void sendDeadlineWarning(Task task, int hoursRemaining) {
        log.info("Sending deadline warning for task: {} ({} hours remaining)", 
            task.getTaskCode(), hoursRemaining);

        Notification notification = Notification.builder()
            .user(task.getAssignee())
            .type(NotificationType.DEADLINE_WARNING)
            .referenceId(task.getId())
            .title("⚠️ Task sắp hết hạn")
            .message(String.format("Task '%s' còn %d giờ nữa hết hạn!", 
                task.getTitle(), hoursRemaining))
            .link("/tasks/" + task.getId())
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();

        notificationRepository.save(notification);
    }

    /**
     * Gửi thông báo task quá hạn
     */
    @Transactional
    public void sendOverdueNotification(Task task) {
        log.info("Sending overdue notification for task: {}", task.getTaskCode());

        // Notify assignee
        createNotification(
            task.getAssignee(),
            NotificationType.TASK_OVERDUE,
            task.getId(),
            "❌ Task đã quá hạn",
            String.format("Task '%s' đã quá hạn deadline!", task.getTitle()),
            "/tasks/" + task.getId()
        );

        // Notify reporter
        if (!task.getReporter().getId().equals(task.getAssignee().getId())) {
            createNotification(
                task.getReporter(),
                NotificationType.TASK_OVERDUE,
                task.getId(),
                "❌ Task của nhân viên đã quá hạn",
                String.format("Task '%s' của %s đã quá hạn!", 
                    task.getTitle(), task.getAssignee().getFullName()),
                "/tasks/" + task.getId()
            );
        }
    }

    /**
     * Gửi thông báo task hoàn thành
     */
    @Transactional
    public void sendTaskCompletedNotification(Task task) {
        log.info("Sending task completed notification for task: {}", task.getTaskCode());

        // Notify reporter
        if (!task.getReporter().getId().equals(task.getAssignee().getId())) {
            createNotification(
                task.getReporter(),
                NotificationType.TASK_COMPLETED,
                task.getId(),
                "✅ Task đã hoàn thành",
                String.format("Task '%s' đã được %s hoàn thành", 
                    task.getTitle(), task.getAssignee().getFullName()),
                "/tasks/" + task.getId()
            );
        }
    }

    // ========== CLEANUP ==========

    /**
     * Xóa notifications cũ (cleanup job)
     */
    @Transactional
    public void deleteOldNotifications(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        log.info("Deleting notifications older than: {}", cutoffDate);
        notificationRepository.deleteOldNotifications(cutoffDate);
    }

    // ========== HELPER METHODS ==========

    private void createNotification(Employee user, NotificationType type, 
                                   Integer referenceId, String title, 
                                   String message, String link) {
        Notification notification = Notification.builder()
            .user(user)
            .type(type)
            .referenceId(referenceId)
            .title(title)
            .message(message)
            .link(link)
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();

        notificationRepository.save(notification);
    }
}