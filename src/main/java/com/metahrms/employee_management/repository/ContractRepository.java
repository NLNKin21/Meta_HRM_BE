package com.metahrms.employee_management.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.metahrms.employee_management.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.Contract;
import com.metahrms.employee_management.entity.ContractType;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer>, JpaSpecificationExecutor<Contract> {

    Optional<Contract> findById(Integer id);

    List<Contract> findByEmpId(Integer empId);

    @Query("""
        SELECT c
        FROM Contract c
        WHERE c.empId = :empId
          AND c.isDeleted = false
    """)
    List<Contract> findActiveContractsByEmpId(@Param("empId") Integer empId);

    List<Contract> findByStatus(ContractStatus status);

    List<Contract> findByContractType(ContractType contractType);

    @Query("""
        SELECT c
        FROM Contract c
        WHERE c.isDeleted = false
          AND c.status = :status
          AND c.endDate BETWEEN :fromDate AND :toDate
    """)
    List<Contract> findContractsExpiringBetween(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("status") ContractStatus status
    );

    @Query("""
        SELECT c
        FROM Contract c
        WHERE c.isDeleted = false
          AND c.status = :status
          AND c.endDate < :date
    """)
    List<Contract> findExpiredContracts(
            @Param("date") LocalDate date,
            @Param("status") ContractStatus status
    );

    // Dashboard queries
    List<Contract> findByIsDeletedAndStatus(boolean isDeleted, ContractStatus status);

    void deleteById(Integer id);

    @Query("""
        SELECT c FROM Contract c
        JOIN FETCH c.contractType ct
        WHERE c.isDeleted = false
    """)
    List<Contract> findAllWithContractType();
}