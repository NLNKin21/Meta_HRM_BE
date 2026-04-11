package com.metahrms.employee_management.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metahrms.employee_management.dto.response.face.FaceEnrollResponseDTO;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.entity.Attendance.EmployeeFace;
import com.metahrms.employee_management.exception.FaceRecognitionException;
import com.metahrms.employee_management.exception.ResourceNotFoundException;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.repository.Attendance.EmployeeFaceRepository;
import com.metahrms.employee_management.service.CloudinaryService;
import com.metahrms.employee_management.service.EmployeeFaceService;
import com.metahrms.employee_management.service.FaceRecognitionClient;
import com.metahrms.employee_management.util.ImageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation của EmployeeFaceService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeFaceServiceImpl implements EmployeeFaceService {
    
    private final EmployeeFaceRepository employeeFaceRepository;
    private final EmployeeRepository employeeRepository;
    private final FaceRecognitionClient faceRecognitionClient;
    private final ObjectMapper objectMapper;
    private final CloudinaryService cloudinaryService;
    
    @Override
    @Transactional
    public EmployeeFace enrollFace(Long employeeId, MultipartFile imageFile, Boolean isPrimary) 
            throws IOException {
        
        log.info("Enrolling face for employee_id={}, isPrimary={}", employeeId, isPrimary);
        
        // 1. Validate employee exists
        Employee employee = employeeRepository.findById(employeeId.intValue())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Employee not found with id: " + employeeId
            ));
        
        // 2. Validate image
        validateImage(imageFile);
        
        // 3. Convert to base64 and resize/compress
        String originalBase64 = ImageUtil.convertToBase64(imageFile);
        String processedBase64 = ImageUtil.resizeAndCompress(originalBase64);
        
        log.debug("Image processed: original_size={} bytes, processed_size={} bytes",
            imageFile.getSize(),
            ImageUtil.decodeBase64(processedBase64).length
        );
        
        // 4. Call Python AI service to get embedding
        FaceEnrollResponseDTO aiResponse = faceRecognitionClient.enrollFace(
            employeeId,
            processedBase64,
            isPrimary
        );
        
        if (!aiResponse.getSuccess()) {
            throw new FaceRecognitionException(
                "Face enrollment failed: " + aiResponse.getMessage()
            );
        }
        
        // 5. Upload image to Cloudinary
        String cloudinaryUrl = uploadImageToCloudinary(imageFile);
        
        log.info("Image uploaded to Cloudinary: {}", cloudinaryUrl);
        
        // 6. Convert embedding to JSON string
        String embeddingJson = convertEmbeddingToJson(
            aiResponse.getData().getEmbedding()
        );
        
        // 7. If isPrimary = true, unset all other primary faces
        if (isPrimary) {
            employeeFaceRepository.unsetAllPrimaryByEmployeeId(employee.getId());
            log.debug("Unset all existing primary faces for employee_id={}", employeeId);
        }
        
        // 8. Create and save EmployeeFace entity
        EmployeeFace employeeFace = EmployeeFace.builder()
            .employeeId(employee.getId())
            .faceImageUrl(cloudinaryUrl)
            .faceEncoding(embeddingJson)
            .confidenceScore(BigDecimal.valueOf(
                aiResponse.getData().getFaceConfidence() * 100
            ))
            .isPrimary(isPrimary)
            .isActive(true)
            .build();
        
        EmployeeFace saved = employeeFaceRepository.save(employeeFace);
        
        log.info("Face enrolled successfully: face_id={}, employee_id={}, confidence={}",
            saved.getId(),
            employeeId,
            saved.getConfidenceScore()
        );
        
        return saved;
    }
    
    @Override
    @Transactional
    public EmployeeFace enrollFaceFromBase64(Long employeeId, String imageBase64, Boolean isPrimary) {
        
        log.info("Enrolling face from base64 for employee_id={}", employeeId);
        
        // 1. Validate employee
        Employee employee = employeeRepository.findById(employeeId.intValue())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Employee not found with id: " + employeeId
            ));
        
        // 2. Resize/compress
        String processedBase64;
        try {
            processedBase64 = ImageUtil.resizeAndCompress(imageBase64);
        } catch (IOException e) {
            throw new FaceRecognitionException("Failed to process image", e);
        }
        
        // 3. Call AI service
        FaceEnrollResponseDTO aiResponse = faceRecognitionClient.enrollFace(
            employeeId,
            processedBase64,
            isPrimary
        );
        
        if (!aiResponse.getSuccess()) {
            throw new FaceRecognitionException(
                "Face enrollment failed: " + aiResponse.getMessage()
            );
        }
        
        // 4. Upload to Cloudinary
        String fileName = "employee_" + employeeId + "_face_" + System.currentTimeMillis();
        String cloudinaryUrl;
        try {
            cloudinaryUrl = uploadBase64ToCloudinary(processedBase64, fileName);
        } catch (IOException e) {
            throw new FaceRecognitionException("Failed to upload image", e);
        }
        
        // 5. Convert embedding
        String embeddingJson = convertEmbeddingToJson(
            aiResponse.getData().getEmbedding()
        );
        
        // 6. Unset primary if needed
        if (isPrimary) {
            employeeFaceRepository.unsetAllPrimaryByEmployeeId(employee.getId());
        }
        
        // 7. Save
        EmployeeFace employeeFace = EmployeeFace.builder()
            .employeeId(employee.getId())
            .faceImageUrl(cloudinaryUrl)
            .faceEncoding(embeddingJson)
            .confidenceScore(BigDecimal.valueOf(
                aiResponse.getData().getFaceConfidence() * 100
            ))
            .isPrimary(isPrimary)
            .isActive(true)
            .build();
        
        return employeeFaceRepository.save(employeeFace);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EmployeeFace> getEmployeeFaces(Long employeeId) {
        return employeeFaceRepository.findByEmployeeId(employeeId.intValue());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EmployeeFace> getActiveFaces(Long employeeId) {
        return employeeFaceRepository.findByEmployeeIdAndIsActiveTrue(employeeId.intValue());
    }
    
    @Override
    @Transactional(readOnly = true)
    public EmployeeFace getPrimaryFace(Long employeeId) {
        return employeeFaceRepository
            .findByEmployeeIdAndIsPrimaryTrueAndIsActiveTrue(employeeId.intValue())
            .orElseThrow(() -> new ResourceNotFoundException(
                "No primary face found for employee_id: " + employeeId
            ));
    }
    
    @Override
    @Transactional
    public EmployeeFace setPrimaryFace(Integer faceId) {
        
        // 1. Find face
        EmployeeFace face = employeeFaceRepository.findById(faceId)
            .orElseThrow(() -> new ResourceNotFoundException("Face not found: " + faceId));
        
        // 2. Unset all primary for this employee
        employeeFaceRepository.unsetAllPrimaryByEmployeeId(face.getEmployee().getId());
        
        // 3. Set this as primary
        face.setIsPrimary(true);
        
        return employeeFaceRepository.save(face);
    }
    
    @Override
    @Transactional
    public void deleteFace(Integer faceId) {
        
        EmployeeFace face = employeeFaceRepository.findById(faceId)
            .orElseThrow(() -> new ResourceNotFoundException("Face not found: " + faceId));
        
        // Soft delete
        employeeFaceRepository.softDelete(faceId);
        
        log.info("Face deleted (soft): face_id={}, employee_id={}", 
            faceId, 
            face.getEmployee().getId()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<List<Double>> getEmployeeEmbeddings(Long employeeId) {
        
        List<EmployeeFace> faces = getActiveFaces(employeeId);
        
        if (faces.isEmpty()) {
            throw new ResourceNotFoundException(
                "No face embeddings found for employee_id: " + employeeId
            );
        }
        
        return faces.stream()
            .map(face -> {
                try {
                    return parseEmbeddingFromJson(face.getFaceEncoding());
                } catch (IOException e) {
                    log.error("Failed to parse embedding for face_id={}", face.getId(), e);
                    return null;
                }
            })
            .filter(emb -> emb != null)
            .collect(Collectors.toList());
    }
    
    @Override
    public String uploadImageToCloudinary(MultipartFile file) throws IOException {
        return cloudinaryService.uploadFile(file, "faces", "face_" + System.currentTimeMillis());
    }

    @Override
    public String uploadBase64ToCloudinary(String base64Image, String fileName) throws IOException {
        return cloudinaryService.uploadBase64(base64Image, "faces", fileName);
    }
    
    // ============================================
    // PRIVATE HELPER METHODS
    // ============================================
    
    /**
     * Validate uploaded image
     */
    private void validateImage(MultipartFile file) {
        
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }
        
        // Validate format
        if (!ImageUtil.validateImageFormat(file)) {
            throw new IllegalArgumentException(
                "Invalid image format. Only JPEG and PNG are supported"
            );
        }
        
        // Validate size (10MB)
        if (!ImageUtil.validateImageSize(file, 10 * 1024 * 1024)) {
            throw new IllegalArgumentException("Image size exceeds 10MB limit");
        }
    }
    
    /**
     * Convert embedding List<Double> to JSON string để lưu vào DB
     */
    private String convertEmbeddingToJson(List<Double> embedding) {
        try {
            return objectMapper.writeValueAsString(embedding);
        } catch (Exception e) {
            throw new FaceRecognitionException("Failed to convert embedding to JSON", e);
        }
    }
    
    /**
     * Parse embedding từ JSON string trong DB
     */
    private List<Double> parseEmbeddingFromJson(String json) throws IOException {
        return objectMapper.readValue(json, new TypeReference<List<Double>>() {});
    }
}