package com.metahrms.employee_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.ContractType;

@Repository
public interface ContractTypeRepository 
        extends JpaRepository<ContractType, Integer>, 
                JpaSpecificationExecutor<ContractType> {

    Optional<ContractType> findByTypeCodeAndIsDeletedFalse(String typeCode);

    boolean existsByTypeCodeAndIsDeletedFalse(String typeCode);

    // Lấy tất cả đang active (dùng cho dropdown chọn loại HĐ)
    @Query("""
        SELECT ct FROM ContractType ct
        WHERE ct.isDeleted = false
          AND ct.isActive = true
        ORDER BY ct.typeName ASC
    """)
    List<ContractType> findAllActiveTypes();

    // Kiểm tra loại HĐ đã được dùng trong contract chưa
    @Query("""
        SELECT COUNT(c) > 0
        FROM Contract c
        WHERE c.contractType.id = :contractTypeId
          AND c.isDeleted = false
    """)
    boolean isUsedInContracts(@Param("contractTypeId") Integer contractTypeId);
}