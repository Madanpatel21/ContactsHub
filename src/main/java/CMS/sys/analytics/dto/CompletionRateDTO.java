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
@Schema(description = "Reminder/follow-up completion analytics")
public class CompletionRateDTO {

    @Schema(description = "Total reminders or follow-up interactions", example = "20")
    private long total;

    @Schema(description = "Completed follow-up interactions", example = "15")
    private long completed;

    @Schema(description = "Completion percentage between 0 and 100", example = "75.0")
    private double completionPercentage;
}