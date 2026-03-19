package com.metahrms.employee_management.service.impl;

import com.metahrms.employee_management.dto.request.Position.PositionDto;
import com.metahrms.employee_management.dto.request.Position.PositionFilterDto;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.dto.response.Position.PositionResponse;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Position;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.PositionRepository;
import com.metahrms.employee_management.service.PositionService;
import com.metahrms.employee_management.specification.PositionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public PagedResponse<PositionResponse> getPositions(PositionFilterDto filterDto) {
        Pageable pageable = PageRequest.of(filterDto.getPage(), filterDto.getPageSize(), Sort.by("id").descending());
        Specification<Position> spec = PositionSpecification.filterPosition(
                filterDto.getSearch(),
                filterDto.getDeptId(),
                filterDto.getIsActive()
        );

        Page<Position> page = positionRepository.findAll(spec, pageable);

        return PagedResponse.<PositionResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).toList())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    @Override
    public PositionResponse getPositionById(Integer id) {
        Position position = positionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Position not found with id: " + id));
        return mapToResponse(position);
    }

    @Override
    @Transactional
    public PositionResponse createPosition(PositionDto createDto) {
        if (positionRepository.existsByPositionCode(createDto.getPositionCode())) {
            throw new RuntimeException("Position code already exists");
        }

        Department department = departmentRepository.findById(createDto.getDeptId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Position position = Position.builder()
                .positionCode(createDto.getPositionCode())
                .positionName(createDto.getPositionName())
                .description(createDto.getDescription())
                .minSalary(createDto.getMinSalary())
                .maxSalary(createDto.getMaxSalary())
                .department(department)
                .build();
        position.setIsDeleted(false);

        return mapToResponse(positionRepository.save(position));
    }

    @Override
    @Transactional
    public PositionResponse updatePosition(Integer id, PositionDto updateDto) {
        Position position = positionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        Department department = departmentRepository.findById(updateDto.getDeptId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        position.setPositionCode(updateDto.getPositionCode());
        position.setPositionName(updateDto.getPositionName());
        position.setDescription(updateDto.getDescription());
        position.setMinSalary(updateDto.getMinSalary());
        position.setMaxSalary(updateDto.getMaxSalary());
        position.setDepartment(department);

        return mapToResponse(positionRepository.save(position));
    }

    @Override
    public void deletePosition(Integer id) {
        Position position = positionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Position not found"));
        position.setIsDeleted(true);
        positionRepository.save(position);
    }

    private PositionResponse mapToResponse(Position position) {
        return PositionResponse.builder()
                .id(position.getId())
                .positionCode(position.getPositionCode())
                .positionName(position.getPositionName())
                .description(position.getDescription())
                .minSalary(position.getMinSalary())
                .maxSalary(position.getMaxSalary())
                .isActive(position.getIsActive())
                .deptId(position.getDepartment() != null ? Math.toIntExact(position.getDepartment().getId()) : null)
                .deptName(position.getDepartment() != null ? position.getDepartment().getDeptName() : null)
                .build();
    }
}