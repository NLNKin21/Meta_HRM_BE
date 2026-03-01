package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.User;
import com.metahrms.employee_management.enums.Role;
import com.metahrms.employee_management.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByResetToken(String resetToken);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByRoleAndIsDeletedFalse(Role role);

    List<User> findByStatusAndIsDeletedFalse(UserStatus status);

    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :loginTime WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = u.failedLoginAttempts + 1 WHERE u.username = :username")
    void incrementFailedAttempts(@Param("username") String username);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = 0, u.lockTime = null WHERE u.username = :username")
    void resetFailedAttempts(@Param("username") String username);
}