package com.metahrms.employee_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found in the system
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Constructor with simple message
     * 
     * @param message Error message describing what resource was not found
     * 
     * Example:
     * throw new ResourceNotFoundException("Contract not found with id: 123");
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructor with resource details for more specific error messages
     * 
     * @param resourceName Name of the resource (e.g., "Contract", "Employee")
     * @param fieldName Field used for search (e.g., "id", "empId")
     * @param fieldValue Value that was searched for
     * 
     * Example:
     * throw new ResourceNotFoundException("Contract", "id", 123);
     * → Message: "Contract not found with id : '123'"
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
    
    /**
     * Constructor with cause (for exception chaining)
     * 
     * @param message Error message
     * @param cause Original exception that caused this error
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}