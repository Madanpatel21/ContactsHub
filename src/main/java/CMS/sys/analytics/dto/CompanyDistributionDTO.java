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
@Schema(description = "Company distribution analytics item")
public class CompanyDistributionDTO {

    @Schema(description = "Company name", example = "Acme Corp")
    private String company;

    @Schema(description = "Contact count in this company", example = "5")
    private long contactCount;
}