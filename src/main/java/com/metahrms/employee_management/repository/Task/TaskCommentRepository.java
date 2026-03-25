package com.metahrms.employee_management.repository.Task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Task.TaskComment;

import java.util.List;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Integer> {

    @Query("SELECT tc FROM TaskComment tc " +
           "JOIN FETCH tc.user " +
           "WHERE tc.task.id = :taskId " +
           "AND tc.isDeleted = false " +
           "ORDER BY tc.createdAt DESC")
    List<TaskComment> findByTaskIdOrderByCreatedAtDesc(@Param("taskId") Integer taskId);

    @Query("SELECT COUNT(tc) FROM TaskComment tc " +
           "WHERE tc.task.id = :taskId AND tc.isDeleted = false")
    Long countByTaskId(@Param("taskId") Integer taskId);

    @Query("SELECT tc FROM TaskComment tc " +
           "WHERE tc.user.id = :userId " +
           "AND tc.isDeleted = false " +
           "ORDER BY tc.createdAt DESC")
    List<TaskComment> findByUserId(@Param("userId") Integer userId);
}