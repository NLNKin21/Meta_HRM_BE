package com.metahrms.employee_management.specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.metahrms.employee_management.entity.Contract;
import com.metahrms.employee_management.enums.ContractStatus;

import jakarta.persistence.criteria.Predicate;

public class ContractSpecification {

    public static Specification<Contract> filterContracts(
            ContractStatus status,
            Integer contractTypeId,
            Integer empId,
            LocalDate startDate,
            LocalDate endDate) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter out deleted contracts
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));

            // Filter by status
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Filter by contract type
            if (contractTypeId != null) {
                predicates.add(criteriaBuilder.equal(root.get("contractType"), contractTypeId));
            }

            // Filter by employee ID
            if (empId != null) {
                predicates.add(criteriaBuilder.equal(root.get("empId"), empId));
            }

            // Filter by start date
            if (startDate != null) {
                predicates.add(criteriaBuilder.equal(root.get("startDate"), startDate));
            }

            // Filter by end date
            if (endDate != null) {
                predicates.add(criteriaBuilder.equal(root.get("endDate"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
