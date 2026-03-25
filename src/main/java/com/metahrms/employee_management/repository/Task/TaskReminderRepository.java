package com.metahrms.employee_management.repository.Task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Task.TaskReminder;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskReminderRepository extends JpaRepository<TaskReminder, Integer> {

    @Query("SELECT tr FROM TaskReminder tr " +
           "WHERE tr.task.id = :taskId " +
           "AND tr.isSent = false")
    List<TaskReminder> findPendingRemindersByTaskId(@Param("taskId") Integer taskId);

    @Query("SELECT tr FROM TaskReminder tr " +
           "JOIN FETCH tr.task t " +
           "JOIN FETCH t.assignee " +
           "WHERE tr.remindAt <= :now " +
           "AND tr.isSent = false")
    List<TaskReminder> findRemindersToSend(@Param("now") LocalDateTime now);

    @Query("SELECT tr FROM TaskReminder tr " +
           "WHERE tr.task.id = :taskId")
    List<TaskReminder> findByTaskId(@Param("taskId") Integer taskId);
}