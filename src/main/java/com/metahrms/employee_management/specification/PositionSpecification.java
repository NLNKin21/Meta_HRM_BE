package com.metahrms.employee_management.specification;

import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Position;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PositionSpecification {

    public static Specification<Position> filterPosition(
        String search, 
        Integer deptId, 
        Boolean isActive,
        Integer parentPositionId, 
        Integer levelOrder
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Soft delete check
            predicates.add(cb.isFalse(root.get("isDeleted")));

            // Search by code or name
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate codePredicate = cb.like(
                    cb.lower(root.get("positionCode")), 
                    searchPattern
                );
                Predicate namePredicate = cb.like(
                    cb.lower(root.get("positionName")), 
                    searchPattern
                );
                predicates.add(cb.or(codePredicate, namePredicate));
            }

            // Filter by department
            if (deptId != null) {
                predicates.add(cb.equal(root.get("department").get("id"), deptId));
            }

            // Filter by isActive
            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            // ===== THÊM MỚI =====
            
            // Filter by parent position
            if (parentPositionId != null) {
                if (parentPositionId == 0) {
                    // 0 = root positions
                    predicates.add(cb.isNull(root.get("parentPosition")));
                } else {
                    predicates.add(cb.equal(
                        root.get("parentPosition").get("id"), 
                        parentPositionId
                    ));
                }
            }

            // Filter by level
            if (levelOrder != null) {
                predicates.add(cb.equal(root.get("levelOrder"), levelOrder));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}