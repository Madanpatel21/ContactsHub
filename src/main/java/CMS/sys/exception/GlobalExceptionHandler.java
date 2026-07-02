package CMS.sys.exception;

import CMS.sys.dto.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for centralized error handling across all controllers.
 * Translates exceptions into appropriate API error responses.
 * 
 * Design Considerations:
 * - Returns consistent error format using ApiResponse
 * - Logs exceptions for debugging and monitoring
 * - Handles validation errors with field-level details
 * - Easily extensible for additional exception types
 * 
 * @author Contact Management System
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        List<ValidationErrorResponse> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapToValidationError)
                .collect(Collectors.toList());

        log.warn("Validation failed for fields: {}", 
                errors.stream().map(ValidationErrorResponse::getField).collect(Collectors.toList()));

        ValidationErrorResponseWrapper wrapper = new ValidationErrorResponseWrapper(errors);
        ApiResponse<ValidationErrorResponseWrapper> response = ApiResponse.success(
                wrapper,
                "Validation failed"
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {
        
        log.warn("Resource not found: {}", ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResourceException(
            DuplicateResourceException ex,
            WebRequest request) {
        
        log.warn("Duplicate resource: {}", ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        log.error("Unexpected error occurred", ex);
        
        ApiResponse<Void> response = ApiResponse.error("An unexpected error occurred");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ValidationErrorResponse mapToValidationError(FieldError fieldError) {
        return ValidationErrorResponse.of(
                fieldError.getField(),
                fieldError.getRejectedValue(),
                fieldError.getDefaultMessage()
        );
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationErrorResponseWrapper {
        private List<ValidationErrorResponse> errors;
    }
}