package CMS.sys.ai.dto;

import CMS.sys.entity.ContactInteraction;
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
@Schema(description = "AI follow-up suggestion response")
public class AiFollowUpResponse {

    @Schema(description = "Suggested follow-up summary", example = "Schedule Q3 planning call")
    private String suggestion;

    @Schema(description = "Confidence score between 0 and 1", example = "0.82")
    private double confidence;

    @Schema(description = "Suggested due date in ISO format", example = "2024-02-15")
    private String suggestedDueDate;

    @Schema(description = "Contact IDs relevant to the suggestion", example = "[\"contact-1\"]")
    private List<String> relatedContactIds;

    @Schema(description = "Matched interaction types", example = "MEETING")
    private List<ContactInteraction.InteractionType> matchedInteractionTypes;
}