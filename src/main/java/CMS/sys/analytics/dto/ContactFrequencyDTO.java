package CMS.sys.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Contact frequency analytics item")
public class ContactFrequencyDTO {

    @Schema(description = "Contact ID", example = "507f1f77bcf86cd799439011")
    private String contactId;

    @Schema(description = "Contact full name", example = "John Doe")
    private String contactName;

    @Schema(description = "Company name", example = "Acme Corp")
    private String company;

    @Schema(description = "Total interaction count", example = "12")
    private long interactionCount;

    @Schema(description = "Most recent interaction date", example = "2026-06-28")
    private String lastInteractionDate;
}