package CMS.sys.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for AI-powered contact enrichment and smart features.
 *
 * @author Contact Management System
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI request payload")
public class AiContactRequest {

    @NotBlank(message = "Prompt is required")
    @Size(max = 2000, message = "Prompt must not exceed 2000 characters")
    @Schema(description = "Natural language prompt for AI assistance", example = "Suggest a professional email signature for John Doe, Senior Engineer at Acme Corp")
    private String prompt;

    @Schema(description = "Optional contact context to improve AI response accuracy", example = "Working in tech industry")
    private String context;
}