package CMS.sys.exception;

/**
 * Custom exception thrown when attempting to create or update a resource
 * that would result in a duplicate entry (e.g., duplicate email or mobile number).
 * Used to provide meaningful error messages for 409 Conflict responses.
 * 
 * @author Contact Management System
 * @version 1.0
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Constructs a new DuplicateResourceException with the specified detail message.
     * 
     * @param message the detail message explaining the duplicate resource
     */
    public DuplicateResourceException(String message) {
        super(message);
    }

    /**
     * Constructs a new DuplicateResourceException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the duplicate resource
     * @param cause the cause of this exception
     */
    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}