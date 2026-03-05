package com.metahrms.employee_management.specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.enums.EmployeeStatus;

import jakarta.persistence.criteria.Predicate;

public class EmployeeSpecification {

    public static Specification<Employee> filterEmployees(
            EmployeeStatus status,
            Integer deptId,
            LocalDate hireDate,
            String search) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter out deleted employees
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));

            // Filter by status
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Filter by department
            if (deptId != null) {
                predicates.add(criteriaBuilder.equal(root.get("deptId"), deptId));
            }

            // Filter by hire date
            if (hireDate != null) {
                predicates.add(criteriaBuilder.equal(root.get("hireDate"), hireDate));
            }

            // Search in full name (case-insensitive)
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("fullName")),
                        searchPattern
                    )
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
