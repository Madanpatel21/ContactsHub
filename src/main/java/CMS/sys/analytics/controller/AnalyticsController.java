package CMS.sys.analytics.controller;

import CMS.sys.constant.ApiConstants;
import CMS.sys.analytics.dto.*;
import CMS.sys.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(ApiConstants.ANALYTICS_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Contact and interaction analytics APIs")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "Get overview analytics dashboard")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Analytics overview",
                    content = @Content(schema = @Schema(implementation = Object.class))
            )
    })
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        return ResponseEntity.ok(analyticsService.getOverview());
    }

    @Operation(summary = "Get most contacted contacts")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Most contacted contacts",
                    content = @Content(schema = @Schema(implementation = ContactFrequencyDTO.class))
            )
    })
    @GetMapping("/most-contacted")
    public ResponseEntity<List<ContactFrequencyDTO>> getMostContacted(
            @Parameter(description = "Maximum number of results", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.getMostContacted(limit));
    }

    @Operation(summary = "Get least contacted contacts")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Least contacted contacts",
                    content = @Content(schema = @Schema(implementation = ContactFrequencyDTO.class))
            )
    })
    @GetMapping("/least-contacted")
    public ResponseEntity<List<ContactFrequencyDTO>> getLeastContacted(
            @Parameter(description = "Maximum number of results", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.getLeastContacted(limit));
    }

    @Operation(summary = "Get contacts grouped by company")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Contacts by company distribution",
                    content = @Content(schema = @Schema(implementation = CompanyDistributionDTO.class))
            )
    })
    @GetMapping("/contacts-by-company")
    public ResponseEntity<List<CompanyDistributionDTO>> getContactsByCompany() {
        return ResponseEntity.ok(analyticsService.getContactsByCompany());
    }

    @Operation(summary = "Get contacts grouped by city")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Contacts by city distribution",
                    content = @Content(schema = @Schema(implementation = CityDistributionDTO.class))
            )
    })
    @GetMapping("/contacts-by-city")
    public ResponseEntity<List<CityDistributionDTO>> getContactsByCity() {
        return ResponseEntity.ok(analyticsService.getContactsByCity());
    }

    @Operation(summary = "Get monthly additions trend")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Monthly additions",
                    content = @Content(schema = @Schema(implementation = MonthlyAdditionsDTO.class))
            )
    })
    @GetMapping("/monthly-additions")
    public ResponseEntity<List<MonthlyAdditionsDTO>> getMonthlyAdditions() {
        return ResponseEntity.ok(analyticsService.getMonthlyAdditions());
    }

    @Operation(summary = "Get interaction heatmap by day and hour")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Interaction heatmap",
                    content = @Content(schema = @Schema(implementation = InteractionHeatmapDTO.class))
            )
    })
    @GetMapping("/interaction-heatmap")
    public ResponseEntity<List<InteractionHeatmapDTO>> getInteractionHeatmap() {
        return ResponseEntity.ok(analyticsService.getInteractionHeatmap());
    }

    @Operation(summary = "Get reminder/follow-up completion rate")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Reminder completion rate",
                    content = @Content(schema = @Schema(implementation = CompletionRateDTO.class))
            )
    })
    @GetMapping("/reminder-completion-rate")
    public ResponseEntity<CompletionRateDTO> getReminderCompletionRate() {
        return ResponseEntity.ok(analyticsService.getReminderCompletionRate());
    }
}