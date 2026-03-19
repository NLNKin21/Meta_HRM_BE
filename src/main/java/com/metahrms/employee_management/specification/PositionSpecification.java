package com.metahrms.employee_management.specification;

import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Position;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PositionSpecification {

    public static Specification<Position> filterPosition(String search, Integer deptId, Boolean isActive) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Soft delete check
            predicates.add(cb.isFalse(root.get("isDeleted")));

            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("positionCode")), searchPattern),
                        cb.like(cb.lower(root.get("positionName")), searchPattern)
                ));
            }

            if (deptId != null) {
                Join<Position, Department> deptJoin = root.join("department");
                predicates.add(cb.equal(deptJoin.get("id"), deptId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}