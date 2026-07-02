package CMS.sys.geo.controller;

import CMS.sys.constant.ApiConstants;
import CMS.sys.dto.ApiResponse;
import CMS.sys.geo.dto.GeoSearchRequest;
import CMS.sys.geo.service.GeoService;
import CMS.sys.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ApiConstants.API_VERSION + "/geo")
@RequiredArgsConstructor
@Tag(name = "Geo", description = "Geospatial contact search APIs")
public class GeoController {

    private final ContactService contactService;
    private final GeoService geoService;

    @Operation(summary = "Find contacts within a geographic radius")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Nearby contacts found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid coordinates"
            )
    })
    @PostMapping("/nearby")
    public ResponseEntity<ApiResponse<?>> findNearby(@Valid @RequestBody GeoSearchRequest request) {
        double radiusKm = request.getRadiusKm() != null ? request.getRadiusKm() : 10.0;

        var nearby = contactService.findContactsNearby(
                request.getLatitude(),
                request.getLongitude(),
                radiusKm
        );

        return ResponseEntity.ok(ApiResponse.success(nearby, "Nearby contacts found"));
    }
}