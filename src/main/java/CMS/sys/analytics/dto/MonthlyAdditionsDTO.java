package CMS.sys.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Monthly additions analytics item")
public class MonthlyAdditionsDTO {

    @Schema(description = "Month label", example = "2026-06")
    private String month;

    @Schema(description = "Number of contacts added in this month", example = "4")
    private long count;
}