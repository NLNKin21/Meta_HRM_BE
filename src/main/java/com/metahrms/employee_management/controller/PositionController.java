package com.metahrms.employee_management.controller;

import com.metahrms.employee_management.dto.request.Position.PositionDto;
import com.metahrms.employee_management.dto.request.Position.PositionFilterDto;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.PagedResponse;
import com.metahrms.employee_management.dto.response.Position.PositionResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Position", description = "APIs for managing positions")
@RestController
@RequestMapping("/positions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PositionController {

    PositionService positionService;

    @Operation(summary = "Get all positions", description = "Retrieve a paginated list of positions with optional filtering")
    @GetMapping
    public ApiResponse<PagedResponse<PositionResponse>> getPositions(
            @Parameter(description = "Page number (zero-based)", example = "0") @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @Parameter(description = "Filter by search term (code/name)") @RequestParam(name = "search", required = false) String search,
            @Parameter(description = "Filter by department ID") @RequestParam(name = "deptId", required = false) Integer deptId) {

        PositionFilterDto filterDto = PositionFilterDto.builder()
                .page(page)
                .pageSize(pageSize)
                .search(search)
                .deptId(deptId)
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
            @Parameter(description = "Position ID", required = true, example = "1") @PathVariable("id") Integer id) {
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
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Position creation data", required = true, content = @Content(schema = @Schema(implementation = PositionDto.class)))
            @Valid @RequestBody PositionDto createDto) {
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
            @Parameter(description = "Position ID", required = true, example = "1") @PathVariable("id") Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Position update data", required = true, content = @Content(schema = @Schema(implementation = PositionDto.class)))
            @Valid @RequestBody PositionDto updateDto) {
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
            @Parameter(description = "Position ID", required = true, example = "1") @PathVariable("id") Integer id) {
        positionService.deletePosition(id);

        return ApiResponse.<Void>builder()
                .status("success")
                .message("Position deleted successfully")
                .build();
    }
}