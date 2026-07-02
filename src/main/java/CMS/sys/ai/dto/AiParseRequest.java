package CMS.sys.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OCR/vendor parsing request payload")
public class AiParseRequest {

    @NotBlank(message = "text is required")
    @Size(max = 4000, message = "text must not exceed 4000 characters")
    @Schema(description = "Raw card text, transcript, or OCR text", example = "John Doe, Acme Corp, john@acme.com")
    private String text;

    @Schema(description = "Optional source hint", example = "business_card")
    private String source;
}