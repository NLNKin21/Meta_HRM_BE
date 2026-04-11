package com.metahrms.employee_management.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties cho Face Recognition Service
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "face-recognition")
public class FaceRecognitionProperties {
    
    private ServiceConfig service;
    private EndpointsConfig endpoints;
    private TimeoutsConfig timeouts;
    private RetryConfig retry;
    private ThresholdsConfig thresholds;
    private ImageConfig image;
    private CircuitBreakerConfig circuitBreaker;
    
    @Data
    public static class ServiceConfig {
        private String baseUrl;
        private Boolean enabled;
    }
    
    @Data
    public static class EndpointsConfig {
        private String enroll;
        private String verify;
        private String health;
    }
    
    @Data
    public static class TimeoutsConfig {
        private Integer connection;
        private Integer read;
    }
    
    @Data
    public static class RetryConfig {
        private Integer maxAttempts;
        private Integer delay;
    }
    
    @Data
    public static class ThresholdsConfig {
        private Double verification;
        private Double minQuality;
    }
    
    @Data
    public static class ImageConfig {
        private Long maxSize;
        private Double compressQuality;
        private Integer targetWidth;
    }
    
    @Data
    public static class CircuitBreakerConfig {
        private Boolean enabled;
        private Integer failureRateThreshold;
        private Integer waitDurationInOpenState;
        private Integer slidingWindowSize;
    }
}