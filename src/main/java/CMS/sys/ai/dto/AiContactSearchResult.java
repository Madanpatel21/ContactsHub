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
@Schema(description = "AI contact search result")
public class AiContactSearchResult {

    @Schema(description = "Matching contact", implementation = Contact.class)
    private Contact contact;

    @Schema(description = "Match relevance score between 0 and 1", example = "0.88")
    private double relevanceScore;

    @Schema(description = "Matched fields summary", example = "name, company, notes")
    private List<String> matchedFields;
}