package CMS.sys.exception;

/**
 * Custom exception thrown when a requested resource is not found in the database.
 * Used to provide meaningful error messages for 404 responses.
 * 
 * @author Contact Management System
 * @version 1.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     * 
     * @param message the detail message explaining which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     * 
     * @param message the detail message explaining which resource was not found
     * @param cause the cause of this exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}