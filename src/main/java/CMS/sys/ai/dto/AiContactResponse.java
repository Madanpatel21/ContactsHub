package CMS.sys.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for AI-generated suggestions.
 *
 * @author Contact Management System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI-generated response payload")
public class AiContactResponse {

    @Schema(description = "Generated AI response text", example = "Here is a suggested email signature...")
    private String response;

    @Schema(description = "Model used for generation", example = "qwen2.5-coder:1.5b")
    private String model;

    @Schema(description = "Processing duration in milliseconds", example = "1240")
    private long durationMs;
}