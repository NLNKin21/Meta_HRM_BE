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

            // ✅ contractType là @ManyToOne → join vào id
            if (contractTypeId != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("contractType").get("id"), contractTypeId
                ));
            }

            // ✅ empId là @Column Integer → so sánh trực tiếp
            if (empId != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("empId"), empId
                ));
            }

            // ✅ startDate: hợp đồng bắt đầu TỪ ngày này trở đi
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("startDate"), startDate
                ));
            }

            // ✅ endDate: hợp đồng kết thúc TRƯỚC hoặc ĐÚNG ngày này
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("endDate"), endDate
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}