package com.metahrms.employee_management.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.metahrms.employee_management.entity.EmployeeDocument;
import com.metahrms.employee_management.enums.DocumentType;

import jakarta.persistence.criteria.Predicate;

public class EmployeeDocumentSpecification {

    public static Specification<EmployeeDocument> filterDocuments(
            Integer empId,
            DocumentType docType) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));

            // Filter by employee ID
            if (empId != null) {
                predicates.add(criteriaBuilder.equal(root.get("empId"), empId));
            }

            // Filter by document type
            if (docType != null) {
                predicates.add(criteriaBuilder.equal(root.get("docType"), docType));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
