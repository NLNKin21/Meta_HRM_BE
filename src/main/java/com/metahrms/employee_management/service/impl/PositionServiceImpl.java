package com.metahrms.employee_management.service.impl;

import com.metahrms.employee_management.dto.request.Position.PositionDto;
import com.metahrms.employee_management.dto.request.Position.PositionFilterDto;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.dto.response.Position.PositionResponse;
import com.metahrms.employee_management.dto.response.Position.PositionTreeResponse;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Position;
import com.metahrms.employee_management.exception.BusinessException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;

    // ===== CRUD CŨ (GIỮ NGUYÊN + CẬP NHẬT NHẸ) =====

    @Override
    public PagedResponse<PositionResponse> getPositions(PositionFilterDto filterDto) {
        Pageable pageable = PageRequest.of(
            filterDto.getPage(), 
            filterDto.getPageSize(), 
            Sort.by("levelOrder").ascending()
                .and(Sort.by("sortOrder").ascending())
                .and(Sort.by("id").descending())
        );
        
        Specification<Position> spec = PositionSpecification.filterPosition(
                filterDto.getSearch(),
                filterDto.getDeptId(),
                filterDto.getIsActive(),
                filterDto.getParentPositionId(),  // THÊM
                filterDto.getLevelOrder()         // THÊM
        );

        Page<Position> page = positionRepository.findAll(spec, pageable);

       return PagedResponse.<PositionResponse>builder()
                .content(page.getContent().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList()))
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PositionResponse getPositionById(Integer id) {
        Position position = positionRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with id: " + id));
        return mapToResponse(position);
    }

    @Override
    @Transactional
    public PositionResponse createPosition(PositionDto createDto) {
        log.info("Creating new position with code: {}", createDto.getPositionCode());
        
        // Validate position code uniqueness
        if (positionRepository.existsByPositionCode(createDto.getPositionCode())) {
            throw new BusinessException("Position code already exists: " + createDto.getPositionCode());
        }

        // Get department
        Department department = departmentRepository.findById(createDto.getDeptId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + createDto.getDeptId()));

        // Get parent position if exists
        Position parentPosition = null;
        if (createDto.getParentPositionId() != null) {
            parentPosition = positionRepository.findByIdAndIsDeletedFalse(createDto.getParentPositionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent position not found"));
            
            // Validate department consistency
            if (!parentPosition.getDepartment().getId().equals(createDto.getDeptId())) {
                throw new BusinessException("Parent position must belong to the same department");
            }
        }

        // Validate salary range
        validateSalaryRange(createDto.getMinSalary(), createDto.getMaxSalary());

        Position position = Position.builder()
                .positionCode(createDto.getPositionCode())
                .positionName(createDto.getPositionName())
                .description(createDto.getDescription())
                .minSalary(createDto.getMinSalary())
                .maxSalary(createDto.getMaxSalary())
                .department(department)
                .parentPosition(parentPosition)
                .levelOrder(calculateLevelOrder(parentPosition))
                .sortOrder(createDto.getSortOrder() != null ? createDto.getSortOrder() : 0)
                .isActive(true)
                .build();
        // Set isDeleted từ BaseEntity sau khi build
        position.setIsDeleted(false);

        Position savedPosition = positionRepository.save(position);
        log.info("Position created successfully with id: {}", savedPosition.getId());
        
        return mapToResponse(savedPosition);
    }

    @Override
    @Transactional
    public PositionResponse updatePosition(Integer id, PositionDto updateDto) {
        log.info("Updating position id: {}", id);
        
        Position position = positionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with id: " + id));

        // Validate position code uniqueness (exclude current)
        if (positionRepository.existsByPositionCodeAndIdNot(updateDto.getPositionCode(), id)) {
            throw new BusinessException("Position code already exists: " + updateDto.getPositionCode());
        }

        // Get department
        Department department = departmentRepository.findById(updateDto.getDeptId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + updateDto.getDeptId()));

        // Handle parent position
        Position parentPosition = null;
        if (updateDto.getParentPositionId() != null) {
            // Cannot set itself as parent
            if (updateDto.getParentPositionId().equals(id)) {
                throw new BusinessException("Position cannot be its own parent");
            }
            
            // Check circular reference
            if (isDescendant(id, updateDto.getParentPositionId())) {
                throw new BusinessException("Circular reference detected: Cannot set a descendant as parent");
            }
            
            parentPosition = positionRepository.findByIdAndIsDeletedFalse(updateDto.getParentPositionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent position not found"));
            
            // Validate department consistency
            if (!parentPosition.getDepartment().getId().equals(updateDto.getDeptId())) {
                throw new BusinessException("Parent position must belong to the same department");
            }
        }

        // Validate salary range
        validateSalaryRange(updateDto.getMinSalary(), updateDto.getMaxSalary());

        // Update fields
        position.setPositionCode(updateDto.getPositionCode());
        position.setPositionName(updateDto.getPositionName());
        position.setDescription(updateDto.getDescription());
        position.setMinSalary(updateDto.getMinSalary());
        position.setMaxSalary(updateDto.getMaxSalary());
        position.setDepartment(department);
        position.setParentPosition(parentPosition);
        position.setLevelOrder(calculateLevelOrder(parentPosition));
        position.setSortOrder(updateDto.getSortOrder() != null ? updateDto.getSortOrder() : position.getSortOrder());

        Position savedPosition = positionRepository.save(position);
        log.info("Position updated successfully");
        
        return mapToResponse(savedPosition);
    }

    @Override
    @Transactional
    public void deletePosition(Integer id) {
        log.info("Deleting position id: {}", id);
        
        Position position = positionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with id: " + id));
        
        // Check if position has employees
        Long employeeCount = positionRepository.countEmployeesByPositionId(id);
        if (employeeCount > 0) {
            throw new BusinessException("Cannot delete position with " + employeeCount + " employees");
        }
        
        // Soft delete position and all children
        softDeleteRecursive(position);
        log.info("Position deleted successfully (including {} children)", position.getChildPositions().size());
    }

    // ===== TREE OPERATIONS =====

    @Override
    @Transactional(readOnly = true)
    public List<PositionTreeResponse> getPositionTree() {
        log.info("Fetching complete position tree");
        
        List<Position> rootPositions = positionRepository
                .findByParentPositionIsNullAndIsDeletedFalseOrderBySortOrder();
        
        return rootPositions.stream()
                .map(this::buildTreeRecursive)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PositionTreeResponse> getPositionTreeByDepartment(Integer deptId) {
        log.info("Fetching position tree for department: {}", deptId);
        
        // Validate department exists
        departmentRepository.findById(deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + deptId));
        
        List<Position> rootPositions = positionRepository
                .findByDepartmentIdAndParentPositionIsNullAndIsDeletedFalseOrderBySortOrder(deptId);
        
        return rootPositions.stream()
                .map(this::buildTreeRecursive)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PositionTreeResponse movePosition(Integer positionId, Integer newParentId) {
        log.info("Moving position {} to new parent {}", positionId, newParentId);
        
        Position position = positionRepository.findByIdAndIsDeletedFalse(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found"));

        Position newParent = null;
        if (newParentId != null) {
            // Cannot move to itself
            if (newParentId.equals(positionId)) {
                throw new BusinessException("Cannot move position to itself");
            }
            
            // Cannot move to its descendant
            if (isDescendant(positionId, newParentId)) {
                throw new BusinessException("Cannot move position to its own descendant");
            }
            
            newParent = positionRepository.findByIdAndIsDeletedFalse(newParentId)
                    .orElseThrow(() -> new ResourceNotFoundException("New parent position not found"));
            
            // Must be in same department
            if (!newParent.getDepartment().getId().equals(position.getDepartment().getId())) {
                throw new BusinessException("Cannot move position to different department");
            }
        }

        position.setParentPosition(newParent);
        position.setLevelOrder(calculateLevelOrder(newParent));

        Position saved = positionRepository.save(position);
        log.info("Position moved successfully");
        
        return buildTreeRecursive(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PositionResponse> getAvailableParents(Integer positionId) {
        List<Position> allPositions = positionRepository.findAllWithRelations();
        
        if (positionId == null) {
            // Creating new: allow all positions
            return allPositions.stream()
                    .filter(p -> !p.getIsDeleted())
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        
        // Updating: exclude itself and its descendants
        Position currentPosition = positionRepository.findByIdAndIsDeletedFalse(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found"));
        
        List<Integer> excludeIds = new ArrayList<>();
        excludeIds.add(positionId);
        collectDescendantIds(currentPosition, excludeIds);
        
        return allPositions.stream()
                .filter(p -> !p.getIsDeleted() && !excludeIds.contains(p.getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ===== PRIVATE HELPER METHODS =====

    /**
     * Build tree recursively with lazy loading protection
     */
    private PositionTreeResponse buildTreeRecursive(Position position) {
        List<Position> children = positionRepository
                .findByParentPositionIdAndIsDeletedFalseOrderBySortOrder(position.getId());
        
        List<PositionTreeResponse> childrenResponse = children.stream()
                .map(this::buildTreeRecursive)
                .collect(Collectors.toList());
        
        Long employeeCount = positionRepository.countEmployeesByPositionId(position.getId());

        return PositionTreeResponse.builder()
                .id(position.getId())
                .positionCode(position.getPositionCode())
                .positionName(position.getPositionName())
                .description(position.getDescription())
                .minSalary(position.getMinSalary())
                .maxSalary(position.getMaxSalary())
                .isActive(position.getIsActive())
                .deptId(position.getDepartment() != null ? position.getDepartment().getId() : null)
                .deptName(position.getDepartment() != null ? position.getDepartment().getDeptName() : null)
                .parentPositionId(position.getParentPosition() != null ? position.getParentPosition().getId() : null)
                .parentPositionName(position.getParentPosition() != null ? position.getParentPosition().getPositionName() : null)
                .levelOrder(position.getLevelOrder())
                .sortOrder(position.getSortOrder())
                .children(childrenResponse)
                .employeeCount(employeeCount.intValue())
                .hasChildren(!childrenResponse.isEmpty())
                .build();
    }

    /**
     * Check if childId is descendant of parentId
     */
    private boolean isDescendant(Integer parentId, Integer childId) {
        Position child = positionRepository.findByIdAndIsDeletedFalse(childId).orElse(null);
        
        while (child != null && child.getParentPosition() != null) {
            if (child.getParentPosition().getId().equals(parentId)) {
                return true;
            }
            child = child.getParentPosition();
        }
        
        return false;
    }

    /**
     * Collect all descendant IDs recursively
     */
    private void collectDescendantIds(Position position, List<Integer> ids) {
        for (Position child : position.getChildPositions()) {
            if (!child.getIsDeleted()) {
                ids.add(child.getId());
                collectDescendantIds(child, ids);
            }
        }
    }

    /**
     * Soft delete recursively
     */
    private void softDeleteRecursive(Position position) {
        position.setIsDeleted(true);
        positionRepository.save(position);
        
        for (Position child : position.getChildPositions()) {
            if (!child.getIsDeleted()) {
                softDeleteRecursive(child);
            }
        }
    }

    /**
     * Calculate level order based on parent
     */
    private Integer calculateLevelOrder(Position parent) {
        if (parent == null) {
            return 1;
        }
        return parent.getLevelOrder() + 1;
    }

    /**
     * Validate salary range
     */
    private void validateSalaryRange(BigDecimal minSalary, BigDecimal maxSalary) {
    if (minSalary != null && maxSalary != null && minSalary.compareTo(maxSalary) > 0) {
        throw new BusinessException("Minimum salary cannot be greater than maximum salary");
        }
    }

    /**
     * Map Position to PositionResponse
     */
    private PositionResponse mapToResponse(Position position) {
        return PositionResponse.builder()
                .id(position.getId())
                .positionCode(position.getPositionCode())
                .positionName(position.getPositionName())
                .description(position.getDescription())
                .minSalary(position.getMinSalary())
                .maxSalary(position.getMaxSalary())
                .isActive(position.getIsActive())
                .deptId(position.getDepartment() != null ? position.getDepartment().getId() : null)
                .deptName(position.getDepartment() != null ? position.getDepartment().getDeptName() : null)
                .parentPositionId(position.getParentPosition() != null ? position.getParentPosition().getId() : null)
                .parentPositionName(position.getParentPosition() != null ? position.getParentPosition().getPositionName() : null)
                .levelOrder(position.getLevelOrder())
                .sortOrder(position.getSortOrder())
                .build();
    }
}