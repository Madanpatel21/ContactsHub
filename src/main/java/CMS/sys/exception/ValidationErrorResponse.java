package CMS.sys.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for validation error details.
 * Contains field-level error messages for invalid input.
 * 
 * @author Contact Management System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Validation error details")
public class ValidationErrorResponse {

    @Schema(description = "Name of the field that failed validation", example = "email")
    private String field;

    @Schema(description = "Rejected value that caused validation failure", example = "invalid-email")
    private Object rejectedValue;

    @Schema(description = "Default error message for the validation failure", example = "Invalid email format")
    private String message;

    /**
     * Creates a ValidationErrorResponse for a single field error.
     * 
     * @param field the field name
     * @param rejectedValue the rejected value
     * @param message the error message
     * @return a new ValidationErrorResponse instance
     */
    public static ValidationErrorResponse of(String field, Object rejectedValue, String message) {
        return new ValidationErrorResponse(field, rejectedValue, message);
    }

    /**
     * Creates a ValidationErrorResponse without a rejected value.
     * 
     * @param field the field name
     * @param message the error message
     * @return a new ValidationErrorResponse instance
     */
    public static ValidationErrorResponse of(String field, String message) {
        return new ValidationErrorResponse(field, null, message);
    }
}