package CMS.sys.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper used across all endpoints.
 * Provides a consistent response structure for all API calls.
 * 
 * Design Considerations:
 * - Generic data field supports any response type
 * - Consistent timestamp for all responses
 * - Success flag for easy client-side handling
 * - Message field for human-readable feedback
 * 
 * @author Contact Management System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Indicates if the request was successful", example = "true")
    private Boolean success;

    @Schema(description = "Human-readable response message", example = "Operation completed successfully")
    private String message;

    @Schema(description = "Response data payload")
    private T data;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Timestamp of the response", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}