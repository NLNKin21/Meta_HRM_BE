package com.metahrms.employee_management.service;

import com.metahrms.employee_management.dto.request.location.CreateWorkLocationRequest;
import com.metahrms.employee_management.dto.request.location.UpdateWorkLocationRequest;
import com.metahrms.employee_management.dto.response.location.WorkLocationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkLocationService {

    WorkLocationResponseDTO create(CreateWorkLocationRequest request);

    WorkLocationResponseDTO update(Integer id, UpdateWorkLocationRequest request);

    void softDelete(Integer id);

    WorkLocationResponseDTO getById(Integer id);

    Page<WorkLocationResponseDTO> getAll(Boolean isActive, String keyword, Pageable pageable);

    void activate(Integer id);

    void deactivate(Integer id);
}