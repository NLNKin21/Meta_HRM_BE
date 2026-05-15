package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id NOT IN (SELECT e.userId FROM Employee e WHERE e.isDeleted = false)")
    List<User> findUsersNotLinkedToEmployee();

    boolean existsByUsername(String username);
}