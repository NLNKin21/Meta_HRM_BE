package com.metahrms.employee_management.service;

import com.metahrms.employee_management.dto.request.shift.AssignShiftRequest;
import com.metahrms.employee_management.dto.request.shift.CreateShiftRequest;
import com.metahrms.employee_management.dto.request.shift.UpdateShiftRequest;
import com.metahrms.employee_management.dto.response.shift.ShiftEmployeeDTO;
import com.metahrms.employee_management.dto.response.shift.ShiftResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShiftService {

    ShiftResponseDTO create(CreateShiftRequest request);

    ShiftResponseDTO update(Integer id, UpdateShiftRequest request);

    void softDelete(Integer id);

    ShiftResponseDTO getById(Integer id);

    Page<ShiftResponseDTO> getAll(Boolean isActive, String keyword, Pageable pageable);

    /**
     * Gán ca cho 1 employee
     */
    void assignShiftToEmployee(Integer employeeId, Integer shiftId);

    /**
     * Gán ca cho nhiều employees
     */
    void assignShiftToEmployees(AssignShiftRequest request);

    /**
     * Bỏ gán ca của employee (set shift = null)
     */
    void unassignShift(Integer employeeId);

    /**
     * Lấy danh sách employees đang dùng shift
     */
    List<ShiftEmployeeDTO> getEmployeesByShift(Integer shiftId);
}