package com.metahrms.employee_management.exception;

/**
 * Exception khi Python AI Service không available
 */
public class FaceServiceUnavailableException extends FaceRecognitionException {
    
    public FaceServiceUnavailableException(String message) {
        super(message, "FACE_SERVICE_UNAVAILABLE");
    }
    
    public FaceServiceUnavailableException(String message, Throwable cause) {
        super(message, "FACE_SERVICE_UNAVAILABLE", cause);
    }
}