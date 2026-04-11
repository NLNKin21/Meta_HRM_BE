package com.metahrms.employee_management.exception;

/**
 * Base exception cho Face Recognition operations
 */
public class FaceRecognitionException extends RuntimeException {
    
    private final String errorCode;
    
    public FaceRecognitionException(String message) {
        super(message);
        this.errorCode = "FACE_RECOGNITION_ERROR";
    }
    
    public FaceRecognitionException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public FaceRecognitionException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "FACE_RECOGNITION_ERROR";
    }
    
    public FaceRecognitionException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}