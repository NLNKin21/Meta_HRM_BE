package com.metahrms.employee_management.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.metahrms.employee_management.config.FaceRecognitionProperties;
import com.metahrms.employee_management.dto.request.face.FaceEnrollRequestDTO;
import com.metahrms.employee_management.dto.request.face.FaceVerifyRequestDTO;
import com.metahrms.employee_management.dto.response.face.FaceEnrollResponseDTO;
import com.metahrms.employee_management.dto.response.face.FaceVerifyResponseDTO;
import com.metahrms.employee_management.exception.FaceRecognitionException;
import com.metahrms.employee_management.exception.FaceServiceUnavailableException;
import com.metahrms.employee_management.exception.FaceValidationException;

import java.util.List;

/**
 * Client để gọi Python Face Recognition Service
 * 
 * Features:
 * - Enroll face (đăng ký khuôn mặt)
 * - Verify face (xác thực khuôn mặt)
 * - Health check
 * - Retry logic
 * - Exception handling
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FaceRecognitionClient {
    
    @Qualifier("faceRecognitionRestTemplate")
    private final RestTemplate restTemplate;
    
    private final FaceRecognitionProperties properties;
    
    /**
     * Enroll face - Đăng ký khuôn mặt nhân viên
     * 
     * @param employeeId ID nhân viên
     * @param imageBase64 Ảnh khuôn mặt dạng base64
     * @param isPrimary Có phải ảnh chính không
     * @return FaceEnrollResponseDTO
     * @throws FaceRecognitionException Nếu có lỗi
     */
    public FaceEnrollResponseDTO enrollFace(Long employeeId, String imageBase64, Boolean isPrimary) {
        
        log.info("[FACE-ENROLL] Starting enrollment for employee_id={}", employeeId);
        
        // Check if service is enabled
        if (!properties.getService().getEnabled()) {
            log.warn("Face recognition service is disabled");
            throw new FaceServiceUnavailableException("Face recognition service is disabled");
        }
        
        // Build request
        FaceEnrollRequestDTO request = FaceEnrollRequestDTO.builder()
            .employeeId(employeeId)
            .imageBase64(imageBase64)
            .isPrimary(isPrimary)
            .build();
        
        // Build URL
        String url = properties.getService().getBaseUrl() + properties.getEndpoints().getEnroll();
        
        log.debug("[FACE-ENROLL] Calling: {}", url);
        
        try {
            // Call Python service
            ResponseEntity<FaceEnrollResponseDTO> response = restTemplate.postForEntity(
                url,
                createHttpEntity(request),
                FaceEnrollResponseDTO.class
            );
            
            FaceEnrollResponseDTO responseBody = response.getBody();
            
            if (responseBody == null) {
                throw new FaceRecognitionException("Empty response from face service");
            }
            
            if (!responseBody.getSuccess()) {
                log.error("[FACE-ENROLL] Failed: {}", responseBody.getMessage());
                throw new FaceValidationException(
                    responseBody.getMessage(), 
                    "ENROLLMENT_FAILED"
                );
            }
            
            log.info("[FACE-ENROLL] Success for employee_id={}, confidence={}", 
                employeeId, 
                responseBody.getData().getFaceConfidence()
            );
            
            return responseBody;
            
        } catch (HttpClientErrorException e) {
            log.error("[FACE-ENROLL] Client error: {}", e.getResponseBodyAsString());
            throw new FaceValidationException(
                "Face validation failed: " + e.getResponseBodyAsString(),
                "VALIDATION_ERROR"
            );
            
        } catch (HttpServerErrorException e) {
            log.error("[FACE-ENROLL] Server error: {}", e.getResponseBodyAsString());
            throw new FaceRecognitionException(
                "Face service internal error",
                "SERVER_ERROR",
                e
            );
            
        } catch (ResourceAccessException e) {
            log.error("[FACE-ENROLL] Connection error: {}", e.getMessage());
            throw new FaceServiceUnavailableException(
                "Cannot connect to face recognition service",
                e
            );
        }
    }
    
    /**
     * Verify face - Xác thực khuôn mặt khi chấm công
     * 
     * @param employeeId ID nhân viên
     * @param imageBase64 Ảnh chấm công
     * @param knownEmbeddings Danh sách embeddings đã lưu
     * @return FaceVerifyResponseDTO
     */
    public FaceVerifyResponseDTO verifyFace(
            Long employeeId, 
            String imageBase64, 
            List<List<Double>> knownEmbeddings) {
        
        return verifyFace(employeeId, imageBase64, knownEmbeddings, null);
    }
    
    /**
     * Verify face với custom threshold
     */
    public FaceVerifyResponseDTO verifyFace(
            Long employeeId,
            String imageBase64,
            List<List<Double>> knownEmbeddings,
            Double customThreshold) {
        
        log.info("[FACE-VERIFY] Starting verification for employee_id={}, embeddings_count={}", 
            employeeId, 
            knownEmbeddings.size()
        );
        
        // Check if service is enabled
        if (!properties.getService().getEnabled()) {
            log.warn("Face recognition service is disabled");
            throw new FaceServiceUnavailableException("Face recognition service is disabled");
        }
        
        // Validate inputs
        if (knownEmbeddings == null || knownEmbeddings.isEmpty()) {
            throw new IllegalArgumentException("Known embeddings cannot be empty");
        }
        
        // Use custom threshold or default from config
        Double threshold = customThreshold != null ? 
            customThreshold : 
            properties.getThresholds().getVerification();
        
        // Build request
        FaceVerifyRequestDTO request = FaceVerifyRequestDTO.builder()
            .employeeId(employeeId)
            .imageBase64(imageBase64)
            .knownEmbeddings(knownEmbeddings)
            .verificationThreshold(threshold)
            .build();
        
        // Build URL
        String url = properties.getService().getBaseUrl() + properties.getEndpoints().getVerify();
        
        log.debug("[FACE-VERIFY] Calling: {}", url);
        
        try {
            // Call Python service
            ResponseEntity<FaceVerifyResponseDTO> response = restTemplate.postForEntity(
                url,
                createHttpEntity(request),
                FaceVerifyResponseDTO.class
            );
            
            FaceVerifyResponseDTO responseBody = response.getBody();
            
            if (responseBody == null) {
                throw new FaceRecognitionException("Empty response from face service");
            }
            
            if (!responseBody.getSuccess()) {
                log.warn("[FACE-VERIFY] Processing failed: {}", responseBody.getMessage());
                // Note: Không throw exception vì có thể là không match (hợp lệ)
                return responseBody;
            }
            
            String matchStatus = responseBody.getIsMatch() ? "MATCH" : "NO_MATCH";
            log.info("[FACE-VERIFY] Result for employee_id={}: {}, confidence={}", 
                employeeId,
                matchStatus,
                responseBody.getConfidence()
            );
            
            return responseBody;
            
        } catch (HttpClientErrorException e) {
            log.error("[FACE-VERIFY] Client error: {}", e.getResponseBodyAsString());
            throw new FaceValidationException(
                "Face validation failed: " + e.getResponseBodyAsString(),
                "VALIDATION_ERROR"
            );
            
        } catch (HttpServerErrorException e) {
            log.error("[FACE-VERIFY] Server error: {}", e.getResponseBodyAsString());
            throw new FaceRecognitionException(
                "Face service internal error",
                "SERVER_ERROR",
                e
            );
            
        } catch (ResourceAccessException e) {
            log.error("[FACE-VERIFY] Connection error: {}", e.getMessage());
            throw new FaceServiceUnavailableException(
                "Cannot connect to face recognition service",
                e
            );
        }
    }
    
    /**
     * Health check - Kiểm tra Python service có hoạt động không
     * 
     * @return true nếu healthy
     */
    public boolean checkHealth() {
        
        if (!properties.getService().getEnabled()) {
            return false;
        }
        
        String url = properties.getService().getBaseUrl() + properties.getEndpoints().getHealth();
        
        try {
            log.debug("[FACE-HEALTH] Checking: {}", url);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            boolean isHealthy = response.getStatusCode() == HttpStatus.OK;
            
            log.info("[FACE-HEALTH] Status: {}", isHealthy ? "HEALTHY" : "UNHEALTHY");
            
            return isHealthy;
            
        } catch (Exception e) {
            log.error("[FACE-HEALTH] Health check failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Create HTTP entity với headers
     */
    private <T> HttpEntity<T> createHttpEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "MetaHRM-Backend/1.0");
        return new HttpEntity<>(body, headers);
    }
}