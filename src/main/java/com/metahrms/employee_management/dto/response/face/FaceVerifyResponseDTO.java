package com.metahrms.employee_management.dto.response.face;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO từ Python AI Service - Verification
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FaceVerifyResponseDTO {
    
    private Boolean success;
    
    private String message;
    
    @JsonProperty("is_match")
    private Boolean isMatch;
    
    private Double confidence;
    
    private VerificationDetailsDTO details;
    
    private List<AnomalyDTO> anomalies;
    
    private LocalDateTime timestamp;
    
    /**
     * Verification details
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VerificationDetailsDTO {
        
        @JsonProperty("euclidean_distance")
        private Double euclideanDistance;
        
        @JsonProperty("cosine_similarity")
        private Double cosineSimilarity;
        
        @JsonProperty("best_match_index")
        private Integer bestMatchIndex;
        
        private String method;
        
        @JsonProperty("num_comparisons")
        private Integer numComparisons;
        
        @JsonProperty("face_confidence")
        private Double faceConfidence;
        
        @JsonProperty("quality_score")
        private Double qualityScore;
        
        @JsonProperty("all_comparisons")
        private List<Map<String, Object>> allComparisons;
    }
    
    /**
     * Anomaly information
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnomalyDTO {
        
        private String type;
        
        private String severity;
        
        private String message;
        
        private Map<String, Object> metadata;
    }
}