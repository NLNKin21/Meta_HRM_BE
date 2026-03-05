package com.metahrms.employee_management.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.User;
import com.metahrms.employee_management.enums.UserStatus;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class UserSpecification {

    public static Specification<User> filterUser(
            UserStatus status,
            Integer deptId,
            String search
    ) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter UserStatus
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // Search by username/email
            if (search != null && !search.isEmpty()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern)
                ));
            }

            if (deptId != null) {
                Join<User, Employee> employeeJoin = root.join("id"); // join với Employee qua userId
                predicates.add(cb.equal(employeeJoin.get("deptId"), deptId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
