package com.metahrms.employee_management.controller;

import com.metahrms.employee_management.dto.request.Position.PositionDto;
import com.metahrms.employee_management.dto.request.Position.PositionFilterDto;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.dto.response.Position.PositionResponse;
import com.metahrms.employee_management.dto.response.Position.PositionTreeResponse;
import com.metahrms.employee_management.entity.Position;
import com.metahrms.employee_management.repository.PositionRepository;
import com.metahrms.employee_management.service.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Position", description = "APIs for managing positions")
@RestController
@RequestMapping("/positions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PositionController {

    PositionService positionService;
    PositionRepository positionRepository;

    // ===== CRUD ENDPOINTS (CŨ - GIỮ NGUYÊN) =====

    @Operation(summary = "Get all positions", description = "Retrieve a paginated list of positions with optional filtering")
    @GetMapping
    public ApiResponse<PagedResponse<PositionResponse>> getPositions(
            @Parameter(description = "Page number (zero-based)", example = "0") 
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            
            @Parameter(description = "Number of items per page", example = "10") 
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            
            @Parameter(description = "Filter by search term (code/name)") 
            @RequestParam(name = "search", required = false) String search,
            
            @Parameter(description = "Filter by department ID") 
            @RequestParam(name = "deptId", required = false) Integer deptId,
            
            @Parameter(description = "Filter by active status") 
            @RequestParam(name = "isActive", required = false) Boolean isActive,
            
            // ===== THÊM MỚI =====
            @Parameter(description = "Filter by parent position ID (0 = root positions)") 
            @RequestParam(name = "parentPositionId", required = false) Integer parentPositionId,
            
            @Parameter(description = "Filter by level order") 
            @RequestParam(name = "levelOrder", required = false) Integer levelOrder
    ) {
        PositionFilterDto filterDto = PositionFilterDto.builder()
                .page(page)
                .pageSize(pageSize)
                .search(search)
                .deptId(deptId)
                .isActive(isActive)
                .parentPositionId(parentPositionId)  // THÊM
                .levelOrder(levelOrder)              // THÊM
                .build();

        PagedResponse<PositionResponse> positions = positionService.getPositions(filterDto);

        return ApiResponse.<PagedResponse<PositionResponse>>builder()
                .status("success")
                .message("Get positions successfully")
                .data(positions)
                .build();
    }

    @Operation(summary = "Get position by ID", description = "Retrieve a single position by its ID")
    @GetMapping("/{id}")
    public ApiResponse<PositionResponse> getPositionById(
            @Parameter(description = "Position ID", required = true, example = "1") 
            @PathVariable("id") Integer id
    ) {
        PositionResponse position = positionService.getPositionById(id);

        return ApiResponse.<PositionResponse>builder()
                .status("success")
                .message("Get position successfully")
                .data(position)
                .build();
    }

    @Operation(summary = "Create position", description = "Create a new position")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PositionResponse> createPosition(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Position creation data", 
                required = true, 
                content = @Content(schema = @Schema(implementation = PositionDto.class))
            )
            @Valid @RequestBody PositionDto createDto
    ) {
        PositionResponse position = positionService.createPosition(createDto);

        return ApiResponse.<PositionResponse>builder()
                .status("success")
                .message("Position created successfully")
                .data(position)
                .build();
    }

    @Operation(summary = "Update position", description = "Update an existing position")
    @PutMapping("/{id}")
    public ApiResponse<PositionResponse> updatePosition(
            @Parameter(description = "Position ID", required = true, example = "1") 
            @PathVariable("id") Integer id,
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Position update data", 
                required = true, 
                content = @Content(schema = @Schema(implementation = PositionDto.class))
            )
            @Valid @RequestBody PositionDto updateDto
    ) {
        PositionResponse position = positionService.updatePosition(id, updateDto);

        return ApiResponse.<PositionResponse>builder()
                .status("success")
                .message("Position updated successfully")
                .data(position)
                .build();
    }

    @Operation(summary = "Delete position", description = "Soft delete a position")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePosition(
            @Parameter(description = "Position ID", required = true, example = "1") 
            @PathVariable("id") Integer id
    ) {
        positionService.deletePosition(id);

        return ApiResponse.<Void>builder()
                .status("success")
                .message("Position deleted successfully")
                .build();
    }

    // ===== TREE ENDPOINTS (MỚI) =====

    @Operation(summary = "Get position tree", description = "Get organization tree of all positions")
    @GetMapping("/tree")
    public ApiResponse<List<PositionTreeResponse>> getPositionTree() {
        List<PositionTreeResponse> tree = positionService.getPositionTree();

        return ApiResponse.<List<PositionTreeResponse>>builder()
                .status("success")
                .message("Get position tree successfully")
                .data(tree)
                .build();
    }

    @Operation(summary = "Get position tree by department", description = "Get organization tree filtered by department")
    @GetMapping("/tree/department/{deptId}")
    public ApiResponse<List<PositionTreeResponse>> getPositionTreeByDepartment(
            @Parameter(description = "Department ID", required = true, example = "1") 
            @PathVariable("deptId") Integer deptId
    ) {
        List<PositionTreeResponse> tree = positionService.getPositionTreeByDepartment(deptId);

        return ApiResponse.<List<PositionTreeResponse>>builder()
                .status("success")
                .message("Get position tree by department successfully")
                .data(tree)
                .build();
    }

    @Operation(summary = "Move position", description = "Move position to a new parent (Drag & Drop)")
    @PutMapping("/{id}/move")
    public ApiResponse<PositionTreeResponse> movePosition(
            @Parameter(description = "Position ID to move", required = true, example = "5") 
            @PathVariable("id") Integer id,
            
            @Parameter(description = "New parent position ID (null = move to root)") 
            @RequestParam(name = "newParentId", required = false) Integer newParentId
    ) {
        PositionTreeResponse position = positionService.movePosition(id, newParentId);

        return ApiResponse.<PositionTreeResponse>builder()
                .status("success")
                .message("Position moved successfully")
                .data(position)
                .build();
    }

    @Operation(summary = "Get available parents", description = "Get list of positions that can be selected as parent")
    @GetMapping("/available-parents")
    public ApiResponse<List<PositionResponse>> getAvailableParents(
            @Parameter(description = "Current position ID (exclude itself and descendants)") 
            @RequestParam(name = "positionId", required = false) Integer positionId
    ) {
        List<PositionResponse> parents = positionService.getAvailableParents(positionId);

        return ApiResponse.<List<PositionResponse>>builder()
                .status("success")
                .message("Get available parents successfully")
                .data(parents)
                .build();
    }

        /**
         * GET /positions/department/{departmentId}
         * Lấy danh sách positions theo phòng ban - dùng cho dropdown khi tạo nhân viên
         */
        @GetMapping("/department/{departmentId}")
        public ApiResponse<List<PositionResponse>> getByDepartment(
                @PathVariable("departmentId") Integer departmentId
        ) {
        List<Position> positions = positionRepository.findByDepartmentIdAndIsDeletedFalseOrderByLevelOrderAscSortOrderAsc(departmentId);

        List<PositionResponse> response = positions.stream()
                .map(p -> PositionResponse.builder()
                        .id(p.getId())
                        .positionCode(p.getPositionCode())
                        .positionName(p.getPositionName())
                        .levelOrder(p.getLevelOrder())
                        .minSalary(p.getMinSalary())
                        .maxSalary(p.getMaxSalary())
                        .isActive(p.getIsActive())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.<List<PositionResponse>>builder()
                .code(200)
                .status("success")
                .message("OK")
                .data(response)
                .build();
        }
}