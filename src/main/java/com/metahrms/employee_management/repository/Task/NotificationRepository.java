package com.metahrms.employee_management.repository.Task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Task.Notification;
import com.metahrms.employee_management.enums.Task.NotificationType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // Lấy notifications của user (có phân trang)
    @Query("SELECT n FROM Notification n " +
           "WHERE n.user.id = :userId " +
           "ORDER BY n.createdAt DESC")
    Page<Notification> findByUserId(@Param("userId") Integer userId, Pageable pageable);

    // Lấy unread notifications
    @Query("SELECT n FROM Notification n " +
           "WHERE n.user.id = :userId " +
           "AND n.isRead = false " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") Integer userId);

    // Đếm unread
    @Query("SELECT COUNT(n) FROM Notification n " +
           "WHERE n.user.id = :userId AND n.isRead = false")
    Long countUnreadByUserId(@Param("userId") Integer userId);

    // Đánh dấu đã đọc
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt " +
           "WHERE n.id = :id")
    void markAsRead(@Param("id") Integer id, @Param("readAt") LocalDateTime readAt);

    // Đánh dấu tất cả đã đọc
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt " +
           "WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsReadByUser(@Param("userId") Integer userId, 
                             @Param("readAt") LocalDateTime readAt);

    // Lấy theo type
    @Query("SELECT n FROM Notification n " +
           "WHERE n.user.id = :userId " +
           "AND n.type = :type " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndType(@Param("userId") Integer userId, 
                                           @Param("type") NotificationType type);

    // Xóa notifications cũ (cleanup)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :beforeDate")
    void deleteOldNotifications(@Param("beforeDate") LocalDateTime beforeDate);
}