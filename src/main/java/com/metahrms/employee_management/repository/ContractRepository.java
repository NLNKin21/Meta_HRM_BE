package com.metahrms.employee_management.repository;

<<<<<<< HEAD
import com.metahrms.employee_management.entity.Contract;
import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.ContractType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

=======
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

<<<<<<< HEAD
public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByIdAndIsDeletedFalse(Long id);

    Optional<Contract> findByContractNumberAndIsDeletedFalse(String contractNumber);

    boolean existsByContractNumberAndIsDeletedFalse(String contractNumber);

    Optional<Contract> findByEmployeeIdAndStatusAndIsDeletedFalse(
            Long employeeId,
            ContractStatus status
    );

    List<Contract> findByEmployeeIdAndIsDeletedFalseOrderByStartDateDesc(Long employeeId);

    List<Contract> findByStatusAndIsDeletedFalse(ContractStatus status);

    Page<Contract> findByIsDeletedFalse(Pageable pageable);

    // expired
    @Query("""
        SELECT c FROM Contract c
        WHERE c.isDeleted = false
        AND c.status = 'ACTIVE'
        AND c.endDate < :today
    """)
    List<Contract> findAllExpired(@Param("today") LocalDate today);

    // expiring soon
    @Query("""
        SELECT c FROM Contract c
        WHERE c.isDeleted = false
        AND c.status = 'ACTIVE'
        AND c.endDate BETWEEN :today AND :futureDate
    """)
    List<Contract> findExpiringContracts(
            @Param("today") LocalDate today,
            @Param("futureDate") LocalDate futureDate
    );

    // search
    @Query("""
        SELECT c FROM Contract c
        WHERE c.isDeleted = false
        AND (:contractNumber IS NULL OR c.contractNumber LIKE %:contractNumber%)
        AND (:status IS NULL OR c.status = :status)
        AND (:contractType IS NULL OR c.contractType = :contractType)
    """)
    Page<Contract> searchContracts(
            @Param("contractNumber") String contractNumber,
            @Param("status") ContractStatus status,
            @Param("contractType") ContractType contractType,
            Pageable pageable
    );
}
=======
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Contract;
import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.ContractType;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer>, JpaSpecificationExecutor<Contract> {

    Optional<Contract> findById(Integer id);

    List<Contract> findByEmpId(Integer empId);

    @Query("SELECT c FROM Contract c WHERE c.empId = :empId AND c.isDeleted = false")
    List<Contract> findActiveContractsByEmpId(@Param("empId") Integer empId);

    List<Contract> findByStatus(ContractStatus status);

    List<Contract> findByContractType(ContractType contractType);

    @Query("SELECT c FROM Contract c WHERE c.endDate < :date AND c.status = :status")
    List<Contract> findExpiringContracts(@Param("date") LocalDate date, @Param("status") ContractStatus status);

    // Dashboard queries
    List<Contract> findByIsDeletedAndStatus(boolean isDeleted, ContractStatus status);

    void deleteById(Integer id);
}
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
