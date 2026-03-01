package com.metahrms.employee_management.repository;

import com.metahrms.employee_management.entity.Contract;
import com.metahrms.employee_management.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByIdAndIsDeletedFalse(Long id);

    Optional<Contract> findByContractNumberAndIsDeletedFalse(String contractNumber);

    List<Contract> findByEmployeeIdAndIsDeletedFalseOrderByStartDateDesc(Long employeeId);

    List<Contract> findByStatusAndIsDeletedFalse(ContractStatus status);

    Optional<Contract> findByEmployeeIdAndStatusAndIsDeletedFalse(Long employeeId, ContractStatus status);

    boolean existsByContractNumber(String contractNumber);

    // Hợp đồng sắp hết hạn
    @Query("SELECT c FROM Contract c WHERE c.status = 'ACTIVE' AND c.isDeleted = false " +
           "AND c.endDate BETWEEN :today AND :futureDate")
    List<Contract> findExpiringContracts(@Param("today") LocalDate today, 
                                         @Param("futureDate") LocalDate futureDate);

    // Hợp đồng đã hết hạn chưa xử lý
    @Query("SELECT c FROM Contract c WHERE c.status = 'ACTIVE' AND c.isDeleted = false " +
           "AND c.endDate < :today")
    List<Contract> findExpiredContracts(@Param("today") LocalDate today);
}
