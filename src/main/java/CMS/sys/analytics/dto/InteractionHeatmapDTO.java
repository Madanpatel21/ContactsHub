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
@Schema(description = "Interaction heatmap item")
public class InteractionHeatmapDTO {

    @Schema(description = "Day of week", example = "Monday")
    private String dayOfWeek;

    @Schema(description = "Hour of day in 24h format", example = "14")
    private int hour;

    @Schema(description = "Interaction count for this slot", example = "3")
    private long count;
}