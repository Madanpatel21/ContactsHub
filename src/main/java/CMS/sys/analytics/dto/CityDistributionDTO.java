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
@Schema(description = "City distribution analytics item")
public class CityDistributionDTO {

    @Schema(description = "City name", example = "New York")
    private String city;

    @Schema(description = "Contact count in this city", example = "8")
    private long contactCount;
}