package CMS.sys.interaction.dto;

import CMS.sys.entity.ContactInteraction.InteractionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating or updating a contact interaction")
public class ContactInteractionRequestDTO {

    @NotNull(message = "Interaction type is required")
    @Schema(description = "Type of interaction", example = "MEETING", allowableValues = {"CALL", "MEETING", "EMAIL", "CHAT", "FOLLOW_UP"})
    private InteractionType type;

    @NotBlank(message = "Subject is required")
    @Size(max = 200, message = "Subject must not exceed 200 characters")
    @Schema(description = "Interaction subject", example = "Quarterly review meeting")
    private String subject;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    @Schema(description = "Detailed interaction notes", example = "Discussed upcoming project timeline and resource allocation")
    private String notes;

    @Size(max = 500, message = "Outcome must not exceed 500 characters")
    @Schema(description = "Meeting or interaction outcome", example = "Agreed to follow up next week with proposal")
    private String outcome;

    @Schema(description = "List of document URLs or file references", example = "[\"https://example.com/doc1.pdf\"]")
    private List<String> attachments;

    @Schema(description = "Date and time of the interaction", example = "2024-01-15T14:30:00")
    private LocalDateTime lastContactedDate;
}