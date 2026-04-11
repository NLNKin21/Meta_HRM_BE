package com.metahrms.employee_management.exception;

/**
 * Exception khi validation fail (no face, multiple faces, low quality...)
 */
public class FaceValidationException extends FaceRecognitionException {
    
    private final String validationType;
    
    public FaceValidationException(String message, String validationType) {
        super(message, "FACE_VALIDATION_ERROR");
        this.validationType = validationType;
    }
    
    public String getValidationType() {
        return validationType;
    }
}