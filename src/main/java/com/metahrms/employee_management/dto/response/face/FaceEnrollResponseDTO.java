package com.metahrms.employee_management.dto.response.face;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO từ Python AI Service - Enrollment
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FaceEnrollResponseDTO {
    
    private Boolean success;
    
    private String message;
    
    private FaceEnrollDataDTO data;
    
    private LocalDateTime timestamp;
    
    /**
     * Nested data object
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FaceEnrollDataDTO {
        
        @JsonProperty("employee_id")
        private Long employeeId;
        
        /**
         * Embedding vector 128 chiều
         */
        private List<Double> embedding;
        
        @JsonProperty("face_confidence")
        private Double faceConfidence;
        
        @JsonProperty("quality_score")
        private Double qualityScore;
        
        @JsonProperty("face_quality_metrics")
        private FaceQualityMetricsDTO faceQualityMetrics;
        
        @JsonProperty("is_primary")
        private Boolean isPrimary;
        
        @JsonProperty("detection_info")
        private Map<String, Object> detectionInfo;
        
        /**
         * Anomalies nếu có
         */
        private Map<String, Object> anomalies;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FaceQualityMetricsDTO {
        private Double confidence;
        private Integer size;
        
        @JsonProperty("area_ratio")
        private Double areaRatio;
        
        @JsonProperty("symmetry_score")
        private Double symmetryScore;
        
        private Double brightness;
    }
}