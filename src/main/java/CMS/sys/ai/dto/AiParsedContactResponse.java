package CMS.sys.ai.dto;

import CMS.sys.dto.ContactRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Structured contact parsed from text/image/voice")
public class AiParsedContactResponse {

    @Schema(description = "Parsed contact fields")
    private ContactRequestDTO contact;

    @Schema(description = "Parsing confidence score between 0 and 1", example = "0.93")
    private double confidence;

    @Schema(description = "Raw extracted snippets", example = "John Doe, john@acme.com")
    private String rawExtract;
}