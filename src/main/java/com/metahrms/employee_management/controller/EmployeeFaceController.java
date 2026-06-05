package com.metahrms.employee_management.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.metahrms.employee_management.dto.request.Attendance.EmployeeFaceDTO;
import com.metahrms.employee_management.dto.request.Attendance.EnrollFaceRequestDTO;
import com.metahrms.employee_management.dto.response.ApiResponse;
import com.metahrms.employee_management.dto.response.Attendance.EnrollFaceResponseDTO;
import com.metahrms.employee_management.entity.Attendance.EmployeeFace;
import com.metahrms.employee_management.service.EmployeeFaceService;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller cho Employee Face Management
 * 
 * Endpoints:
 * - POST /api/faces/upload - Upload ảnh khuôn mặt (multipart/form-data)
 * - POST /api/faces/enroll - Enroll từ base64 (application/json)
 * - GET /api/faces/employee/{employeeId} - Lấy tất cả faces
 * - GET /api/faces/{id} - Lấy face by ID
 * - PUT /api/faces/{id}/primary - Set face làm primary
 * - DELETE /api/faces/{id} - Xóa face
 */
@Slf4j
@RestController
@RequestMapping("/faces")
@RequiredArgsConstructor
@Tag(name = "Employee Face Management", description = "APIs for managing employee face recognition data")
public class EmployeeFaceController {
    
    private final EmployeeFaceService employeeFaceService;
    
    /**
     * Upload ảnh khuôn mặt (Multipart form-data)
     * 
     * Dùng cho web upload hoặc mobile với file picker
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload employee face image",
        description = "Upload face image using multipart/form-data. Image will be processed by AI service to extract face embedding."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<ApiResponse<EnrollFaceResponseDTO>> uploadFace(
            @Parameter(description = "Employee ID", required = true)
            @RequestParam("employeeId") Long employeeId,
            
            @Parameter(description = "Face image file (JPEG/PNG, max 10MB)", required = true)
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "Is this the primary face image")
            @RequestParam(value = "isPrimary", defaultValue = "true") Boolean isPrimary
    ) {
        
        log.info("[FACE-UPLOAD] Received upload request for employee_id={}, file_size={}", 
            employeeId, 
            file.getSize()
        );
        
        try {
            // Enroll face
            EmployeeFace enrolledFace = employeeFaceService.enrollFace(employeeId, file, isPrimary);
            
            // Convert to DTO
            EnrollFaceResponseDTO responseData = EnrollFaceResponseDTO.builder()
                .faceId(enrolledFace.getId())
                .employeeId(employeeId)
                .faceImageUrl(enrolledFace.getFaceImageUrl())
                .confidenceScore(enrolledFace.getConfidenceScore().doubleValue())
                .isPrimary(enrolledFace.getIsPrimary())
                .message("Face enrolled successfully")
                .build();
            
            return ResponseEntity.ok(
                ApiResponse.success(responseData, "Face uploaded and enrolled successfully")
            );
            
        } catch (IOException e) {
            log.error("[FACE-UPLOAD] Failed to process image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    .body(ApiResponse.error(
        500,
        "Failed to process image: " + e.getMessage()
    ));
        }
    }
    
    /**
     * Enroll face từ base64 string (JSON)
     * 
     * Dùng cho mobile apps khi ảnh đã được capture và encode sẵn
     */
    @PostMapping("/enroll")
    @Operation(
        summary = "Enroll face from base64",
        description = "Enroll face using base64-encoded image. Suitable for mobile apps."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<EnrollFaceResponseDTO>> enrollFace(
            @Valid @RequestBody EnrollFaceRequestDTO request
    ) {
        
        log.info("[FACE-ENROLL] Received enroll request for employee_id={}", request.getEmployeeId());
        
        try {
            // Enroll face from base64
            EmployeeFace enrolledFace = employeeFaceService.enrollFaceFromBase64(
                request.getEmployeeId(),
                request.getImageBase64(),
                request.getIsPrimary()
            );
            
            // Convert to response DTO
            EnrollFaceResponseDTO responseData = EnrollFaceResponseDTO.builder()
                .faceId(enrolledFace.getId())
                .employeeId(request.getEmployeeId())
                .faceImageUrl(enrolledFace.getFaceImageUrl())
                .confidenceScore(enrolledFace.getConfidenceScore().doubleValue())
                .isPrimary(enrolledFace.getIsPrimary())
                .message("Face enrolled successfully")
                .build();
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseData, "Face enrolled successfully"));
            
        } catch (Exception e) {
            log.error("[FACE-ENROLL] Enrollment failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(
                500,
                "Face enrollment failed: " + e.getMessage()
    ));
        }
    }
    
    /**
     * Lấy tất cả faces của một employee
     */
    @GetMapping("/employee/{employeeId}")
    @Operation(
        summary = "Get all faces of an employee",
        description = "Retrieve all registered face images for a specific employee"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<EmployeeFaceDTO>>> getEmployeeFaces(
            @Parameter(description = "Employee ID", required = true)
            @PathVariable("employeeId") Long employeeId,
            
            @Parameter(description = "Include inactive faces")
            @RequestParam(name = "includeInactive",value = "includeInactive", defaultValue = "false") Boolean includeInactive
    ) {
        
        log.info("[FACE-GET] Getting faces for employee_id={}, includeInactive={}", 
            employeeId, 
            includeInactive
        );
        
        List<EmployeeFace> faces = includeInactive ? 
            employeeFaceService.getEmployeeFaces(employeeId) : 
            employeeFaceService.getActiveFaces(employeeId);
        
        List<EmployeeFaceDTO> faceDTOs = faces.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success(faceDTOs, "Retrieved " + faceDTOs.size() + " face(s)")
        );
    }
    
    /**
     * Lấy face by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get face by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<ApiResponse<EmployeeFaceDTO>> getFaceById(
            @PathVariable("id") Integer id
    ) {
        
        // TODO: Implement getFaceById in service
        return ResponseEntity.ok(
            ApiResponse.success(null, "Not implemented yet")
        );
    }
    
    /**
     * Set face làm primary
     */
    @PutMapping("/{id}/primary")
    @Operation(
        summary = "Set face as primary",
        description = "Mark a specific face image as the primary one for recognition"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<EmployeeFaceDTO>> setPrimaryFace(
            @Parameter(description = "Face ID", required = true)
            @PathVariable("id") Integer id
    ) {
        
        log.info("[FACE-PRIMARY] Setting face_id={} as primary", id);
        
        EmployeeFace updatedFace = employeeFaceService.setPrimaryFace(id);
        
        return ResponseEntity.ok(
            ApiResponse.success(
                convertToDTO(updatedFace), 
                "Face set as primary successfully"
            )
        );
    }
    
    /**
     * Xóa face (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete face image",
        description = "Soft delete a face image. The image will be marked as inactive."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> deleteFace(
            @Parameter(description = "Face ID", required = true)
            @PathVariable Integer id
    ) {
        
        log.info("[FACE-DELETE] Deleting face_id={}", id);
        
        employeeFaceService.deleteFace(id);
        
        return ResponseEntity.ok(
            ApiResponse.success(null, "Face deleted successfully")
        );
    }
    
    /**
     * Get primary face của employee
     */
    @GetMapping("/employee/{employeeId}/primary")
    @Operation(summary = "Get primary face of employee")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<ApiResponse<EmployeeFaceDTO>> getPrimaryFace(
            @PathVariable Long employeeId
    ) {
        
        log.info("[FACE-PRIMARY-GET] Getting primary face for employee_id={}", employeeId);
        
        EmployeeFace primaryFace = employeeFaceService.getPrimaryFace(employeeId);
        
        return ResponseEntity.ok(
            ApiResponse.success(
                convertToDTO(primaryFace), 
                "Retrieved primary face"
            )
        );
    }
    
    // ============================================
    // HELPER METHODS
    // ============================================
    
    /**
     * Convert Entity to DTO
     */
    private EmployeeFaceDTO convertToDTO(EmployeeFace face) {
        return EmployeeFaceDTO.builder()
            .id(face.getId())
            .employeeId(face.getEmployee().getId().longValue())
            .faceImageUrl(face.getFaceImageUrl())
            .confidenceScore(face.getConfidenceScore() != null ? 
                face.getConfidenceScore().doubleValue() : null)
            .isPrimary(face.getIsPrimary())
            .isActive(face.getIsActive())
            .createdAt(face.getCreatedAt())
            .build();
    }
}