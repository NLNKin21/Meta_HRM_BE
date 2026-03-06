package com.metahrms.employee_management.repository;

<<<<<<< HEAD
import com.metahrms.employee_management.entity.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeDocumentRepository 
        extends JpaRepository<EmployeeDocument, Long> {

    List<EmployeeDocument> findByEmployeeId(Long employeeId);
}
=======
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
>>>>>>> 8c6be7d9d227d6c9e9aae8bb6ebeb4808ead22d3
