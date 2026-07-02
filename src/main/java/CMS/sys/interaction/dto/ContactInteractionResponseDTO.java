package CMS.sys.interaction.dto;

import CMS.sys.entity.ContactInteraction;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Response object containing contact interaction details")
public class ContactInteractionResponseDTO {

    @Schema(description = "Unique identifier of the interaction", example = "507f1f77bcf86cd799439012")
    private String id;

    @Schema(description = "Contact ID this interaction belongs to", example = "507f1f77bcf86cd799439011")
    private String contactId;

    @Schema(description = "Type of interaction", example = "MEETING")
    private ContactInteraction.InteractionType type;

    @Schema(description = "Interaction subject", example = "Quarterly review meeting")
    private String subject;

    @Schema(description = "Detailed interaction notes", example = "Discussed upcoming project timeline")
    private String notes;

    @Schema(description = "Interaction outcome", example = "Agreed to follow up next week")
    private String outcome;

    @Schema(description = "List of attached document URLs", example = "[\"https://example.com/doc1.pdf\"]")
    private List<String> attachments;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date and time of the interaction", example = "2024-01-15T14:30:00")
    private LocalDateTime lastContactedDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Timestamp when the interaction was created", example = "2024-01-15T14:30:00")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Timestamp when the interaction was last updated", example = "2024-01-15T16:00:00")
    private LocalDateTime updatedAt;
}