package com.metahrms.employee_management.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.metahrms.employee_management.entity.ContractType;

import jakarta.persistence.criteria.Predicate;

public class ContractTypeSpecification {

    public static Specification<ContractType> filter(String keyword, Boolean isActive) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Luôn loại deleted
            predicates.add(cb.equal(root.get("isDeleted"), false));

            // Tìm kiếm theo typeCode hoặc typeName
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("typeCode")), pattern),
                    cb.like(cb.lower(root.get("typeName")), pattern)
                ));
            }

            // Filter theo isActive
            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}