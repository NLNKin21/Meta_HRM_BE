package com.metahrms.employee_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metahrms.employee_management.entity.EmployeeDocument;
import com.metahrms.employee_management.enums.DocumentType;

@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Integer>, JpaSpecificationExecutor<EmployeeDocument> {

    Optional<EmployeeDocument> findById(Integer id);

    List<EmployeeDocument> findByEmpId(Integer empId);

    @Query("SELECT ed FROM EmployeeDocument ed WHERE ed.empId = :empId AND ed.isDeleted = false")
    List<EmployeeDocument> findActiveDocumentsByEmpId(@Param("empId") Integer empId);

    List<EmployeeDocument> findByEmpIdAndDocType(Integer empId, DocumentType docType);

    void deleteById(Integer id);
}
