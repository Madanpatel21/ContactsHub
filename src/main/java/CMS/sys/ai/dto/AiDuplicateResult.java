package CMS.sys.ai.dto;

import CMS.sys.entity.Contact;
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
@Schema(description = "AI duplicate detection result")
public class AiDuplicateResult {

    @Schema(description = "Source contact ID being analyzed", example = "contact-1")
    private String sourceContactId;

    @Schema(description = "Probable duplicate contact IDs", example = "[\"contact-2\"]")
    private List<String> probableDuplicateIds;

    @Schema(description = "Similarity score between 0 and 1", example = "0.91")
    private double similarityScore;

    @Schema(description = "Reason for duplicate detection", example = "Same email and similar names")
    private String reason;
}