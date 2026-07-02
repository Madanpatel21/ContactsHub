package CMS.sys.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI-enriched contact result")
public class AiEnrichedContactResult {

    @Schema(description = "Contact ID", example = "contact-1")
    private String contactId;

    @Schema(description = "Suggested smart tags", example = "[\"VIP\", \"Q3 prospect\"]")
    private List<String> suggestedTags;

    @Schema(description = "AI-generated contact notes", example = "Met at conference; interested in pricing.")
    private String generatedNotes;

    @Schema(description = "Suggested follow-up action", example = "Send proposal")
    private String followUpSuggestion;
}