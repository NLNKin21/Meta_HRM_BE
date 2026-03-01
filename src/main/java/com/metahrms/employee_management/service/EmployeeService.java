package com.metahrms.employee_management.service;


import com.metahrms.employee_management.dto.request.EmployeeRequest;
import com.metahrms.employee_management.dto.response.EmployeeResponse;
import com.metahrms.employee_management.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest request);
    
    EmployeeResponse getEmployeeById(Long id);
    
    EmployeeResponse getEmployeeByCode(String employeeCode);
    
    PageResponse<EmployeeResponse> getAllEmployees(Pageable pageable);
    
    PageResponse<EmployeeResponse> searchEmployees(String keyword, Pageable pageable);
    
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    
    void deleteEmployee(Long id);
    
    List<EmployeeResponse> getEmployeesByDepartment(Long departmentId);
    
    List<EmployeeResponse> getSubordinates(Long managerId);
    
    String generateEmployeeCode();
}