package CMS.sys.interaction.controller;

import CMS.sys.constant.ApiConstants;
import CMS.sys.dto.ApiResponse;
import CMS.sys.entity.ContactInteraction;
import CMS.sys.interaction.dto.ContactInteractionRequestDTO;
import CMS.sys.interaction.dto.ContactInteractionResponseDTO;
import CMS.sys.interaction.service.ContactInteractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApiConstants.CONTACTS_BASE_PATH + "/{contactId}/interactions")
@RequiredArgsConstructor
@Tag(name = "Contact Interaction", description = "Contact interaction/history APIs")
public class ContactInteractionController {

    private final ContactInteractionService interactionService;

    @Operation(summary = "Create interaction history for a contact")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Interaction created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ContactInteractionResponseDTO>> createInteraction(
            @Parameter(
                    name = "contactId",
                    description = "Contact ID",
                    in = ParameterIn.PATH,
                    required = true
            )
            @PathVariable String contactId,
            @Valid @RequestBody ContactInteractionRequestDTO requestDTO) {

        ContactInteractionResponseDTO response = interactionService.createInteraction(contactId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Interaction created successfully"));
    }

    @Operation(summary = "Get all interactions for a contact")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Interactions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContactInteractionResponseDTO>>> getInteractions(
            @Parameter(
                    name = "contactId",
                    description = "Contact ID",
                    in = ParameterIn.PATH,
                    required = true
            )
            @PathVariable String contactId) {

        List<ContactInteractionResponseDTO> interactions = interactionService.getInteractionsByContactId(contactId);
        return ResponseEntity.ok(ApiResponse.success(interactions, "Interactions retrieved successfully"));
    }

    @Operation(summary = "Get interactions by type for a contact")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Filtered interactions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<ContactInteractionResponseDTO>>> getInteractionsByType(
            @Parameter(
                    name = "contactId",
                    description = "Contact ID",
                    in = ParameterIn.PATH,
                    required = true
            )
            @PathVariable String contactId,
            @Parameter(
                    name = "type",
                    description = "Interaction type",
                    in = ParameterIn.PATH,
                    required = true,
                    schema = @Schema(type = "string", allowableValues = {"CALL", "MEETING", "EMAIL", "CHAT", "FOLLOW_UP"})
            )
            @PathVariable String type) {

        ContactInteraction.InteractionType interactionType = ContactInteraction.InteractionType.valueOf(type);
        List<ContactInteractionResponseDTO> interactions = interactionService.getInteractionsByContactIdAndType(contactId, interactionType);
        return ResponseEntity.ok(ApiResponse.success(interactions, "Filtered interactions retrieved successfully"));
    }

    @Operation(summary = "Update interaction")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Interaction updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Interaction not found"
            )
    })
    @PutMapping("/{interactionId}")
    public ResponseEntity<ApiResponse<ContactInteractionResponseDTO>> updateInteraction(
            @Parameter(
                    name = "contactId",
                    description = "Contact ID",
                    in = ParameterIn.PATH,
                    required = true
            )
            @PathVariable String contactId,
            @Parameter(
                    name = "interactionId",
                    description = "Interaction ID",
                    in = ParameterIn.PATH,
                    required = true
            )
            @PathVariable String interactionId,
            @Valid @RequestBody ContactInteractionRequestDTO requestDTO) {

        ContactInteractionResponseDTO response = interactionService.updateInteraction(interactionId, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Interaction updated successfully"));
    }

    @Operation(summary = "Get interaction by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Interaction retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Interaction not found"
            )
    })
    @GetMapping("/{interactionId}")
    public ResponseEntity<ApiResponse<ContactInteractionResponseDTO>> getInteraction(
            @Parameter(
                    name = "contactId",
                    description = "Contact ID",
                    in = ParameterIn.PATH,
                    required = true
            )
            @PathVariable String contactId,
            @Parameter(
                    name = "interactionId",
                    description = "Interaction ID",
                    in = ParameterIn.PATH,
                    required = true
            )
            @PathVariable String interactionId) {

        ContactInteractionResponseDTO response = interactionService.getInteractionById(interactionId);
        return ResponseEntity.ok(ApiResponse.success(response, "Interaction retrieved successfully"));
    }

    @Operation(summary = "Delete interaction")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Interaction deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Interaction not found"
            )
    })
    @DeleteMapping("/{interactionId}")
    public ResponseEntity<ApiResponse<Void>> deleteInteraction(
            @Parameter(
                    name = "contactId",
                    description = "Contact ID",
                    in = ParameterIn.PATH,
                    required = true
            )
            @PathVariable String contactId,
            @Parameter(
                    name = "interactionId",
                    description = "Interaction ID",
                    in = ParameterIn.PATH,
                    required = true
            )
            @PathVariable String interactionId) {

        interactionService.deleteInteraction(interactionId);
        return ResponseEntity.ok(ApiResponse.success("Interaction deleted successfully"));
    }
}