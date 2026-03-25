package com.metahrms.employee_management.service.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metahrms.employee_management.entity.Task.Task;
import com.metahrms.employee_management.entity.Task.TaskReminder;
import com.metahrms.employee_management.repository.Task.TaskReminderRepository;
import com.metahrms.employee_management.repository.Task.TaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskReminderService {

    private final TaskReminderRepository reminderRepository;
    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    /**
     * Tạo reminders cho task mới
     */
    @Transactional
    public void createRemindersForTask(Task task) {
        if (task.getDueDate() == null) {
            return;
        }

        log.info("Creating reminders for task: {}", task.getTaskCode());

        // Reminder 24 hours before
        createReminder(task, 24);

        // Reminder 48 hours before
        createReminder(task, 48);

        // Reminder 72 hours before (3 days)
        createReminder(task, 72);
    }

    /**
     * Xử lý reminders (chạy mỗi giờ)
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    @Transactional
    public void processReminders() {
        log.info("Processing pending reminders...");

        LocalDateTime now = LocalDateTime.now();
        List<TaskReminder> remindersToSend = reminderRepository.findRemindersToSend(now);

        log.info("Found {} reminders to process", remindersToSend.size());

        for (TaskReminder reminder : remindersToSend) {
            try {
                Task task = reminder.getTask();
                
                // Skip nếu task đã completed hoặc deleted
                if (task.getStatus().getIsCompleted() || task.getIsDeleted()) {
                    markReminderAsSent(reminder);
                    continue;
                }

                // Calculate hours remaining
                long hoursRemaining = ChronoUnit.HOURS.between(
                    LocalDateTime.now(), 
                    task.getDueDate().atStartOfDay()
                );

                // Send notification
                notificationService.sendDeadlineWarning(task, (int) hoursRemaining);

                // Mark as sent
                markReminderAsSent(reminder);

                log.info("Sent reminder for task: {}", task.getTaskCode());
            } catch (Exception e) {
                log.error("Error processing reminder ID: {}", reminder.getId(), e);
            }
        }
    }

    /**
     * Kiểm tra và cập nhật overdue tasks (chạy mỗi ngày lúc 00:05)
     */
    @Scheduled(cron = "0 5 0 * * *") // Every day at 00:05
    @Transactional
    public void checkOverdueTasks() {
        log.info("Checking overdue tasks...");

        LocalDate today = LocalDate.now();
        List<Task> overdueTasks = taskRepository.findOverdueTasks(today);

        log.info("Found {} overdue tasks", overdueTasks.size());

        for (Task task : overdueTasks) {
            try {
                // Update late status
                if (!task.getIsLate()) {
                    task.setIsLate(true);
                    taskRepository.save(task);

                    // Send overdue notification
                    notificationService.sendOverdueNotification(task);
                }
            } catch (Exception e) {
                log.error("Error processing overdue task ID: {}", task.getId(), e);
            }
        }
    }

    /**
     * Cleanup old reminders (chạy mỗi tuần)
     */
    @Scheduled(cron = "0 0 2 * * SUN") // Every Sunday at 02:00
    @Transactional
    public void cleanupOldReminders() {
        log.info("Cleaning up old notifications...");
        notificationService.deleteOldNotifications(30); // Delete notifications older than 30 days
    }

    // ========== HELPER METHODS ==========

    private void createReminder(Task task, int hoursBefore) {
        LocalDateTime remindAt = task.getDueDate()
            .atStartOfDay()
            .minusHours(hoursBefore);

        // Don't create if remind time is in the past
        if (remindAt.isBefore(LocalDateTime.now())) {
            return;
        }

        TaskReminder reminder = TaskReminder.builder()
            .task(task)
            .remindBeforeHours(hoursBefore)
            .remindAt(remindAt)
            .isSent(false)
            .build();

        reminderRepository.save(reminder);
        log.info("Created reminder for task {} - {} hours before", task.getTaskCode(), hoursBefore);
    }

    private void markReminderAsSent(TaskReminder reminder) {
        reminder.setIsSent(true);
        reminder.setSentAt(LocalDateTime.now());
        reminderRepository.save(reminder);
    }
}