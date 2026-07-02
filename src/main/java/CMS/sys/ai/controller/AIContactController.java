package CMS.sys.ai.controller;

import CMS.sys.constant.ApiConstants;
import CMS.sys.ai.dto.*;
import CMS.sys.ai.service.ContactAiService;
import CMS.sys.dto.ContactRequestDTO;
import CMS.sys.entity.Contact;
import CMS.sys.entity.ContactInteraction;
import CMS.sys.repository.ContactRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApiConstants.API_VERSION + "/ai/contacts")
@RequiredArgsConstructor
@Tag(name = "AI Contact", description = "AI-powered contact assistance APIs")
public class AIContactController {

    private final ContactAiService contactAiService;
    private final ContactRepository contactRepository;

    @Operation(summary = "Generate AI suggestion for contacts")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "AI suggestion generated successfully",
                    content = @Content(schema = @Schema(implementation = AiContactResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid prompt"
            )
    })
    @PostMapping("/suggest")
    public ResponseEntity<AiContactResponse> generateSuggestion(@Valid @RequestBody AiContactRequest request) {
        log.info("Received AI suggestion request: {}", request.getPrompt());
        AiContactResponse response = contactAiService.generateContactSuggestion(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "AI-powered semantic contact search")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "AI contact search completed",
                    content = @Content(schema = @Schema(implementation = AiContactSearchResult.class))
            )
    })
    @PostMapping("/search")
    public ResponseEntity<List<AiContactSearchResult>> aiContactSearch(
            @Parameter(description = "Natural language search query", example = "senior engineer at Acme")
            @RequestParam String query) {

        List<Contact> contacts = contactRepository.findAll();
        List<AiContactSearchResult> results = contactAiService.aiContactSearch(query, contacts);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Detect duplicate contacts using AI heuristics")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Duplicate detection completed",
                    content = @Content(schema = @Schema(implementation = AiDuplicateResult.class))
            )
    })
    @PostMapping("/duplicates")
    public ResponseEntity<List<AiDuplicateResult>> detectDuplicates() {
        List<Contact> contacts = contactRepository.findAll();
        List<AiDuplicateResult> results = contactAiService.detectDuplicates(contacts);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Generate AI-enriched contact profile")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "AI enrichment generated",
                    content = @Content(schema = @Schema(implementation = AiEnrichedContactResult.class))
            )
    })
    @PostMapping("/{contactId}/enrich")
    public ResponseEntity<AiEnrichedContactResult> enrichContact(
            @Parameter(name = "contactId", description = "Contact ID", in = ParameterIn.PATH, required = true)
            @PathVariable String contactId) {

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
        List<ContactInteraction> interactions = List.of();
        AiEnrichedContactResult result = contactAiService.enrichContact(contact, interactions);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Generate meeting summary from notes")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Meeting summary generated",
                    content = @Content(schema = @Schema(implementation = AiContactResponse.class))
            )
    })
    @PostMapping("/meeting-summary")
    public ResponseEntity<AiContactResponse> meetingSummary(@RequestParam String notes) {
        String summary = contactAiService.generateMeetingSummary(notes);
        return ResponseEntity.ok(AiContactResponse.builder()
                .response(summary)
                .model("qwen2.5-coder:1.5b")
                .durationMs(0)
                .build());
    }

    @Operation(summary = "Parse business card or vendor text into structured contact")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Parsed contact generated",
                    content = @Content(schema = @Schema(implementation = AiParsedContactResponse.class))
            )
    })
    @PostMapping("/parse")
    public ResponseEntity<AiParsedContactResponse> parse(@Valid @RequestBody AiParseRequest request) {
        AiParsedContactResponse response = contactAiService.parseBusinessCard(request.getText());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Voice search transcript parser stub")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Transcript parsed",
                    content = @Content(schema = @Schema(implementation = AiParsedContactResponse.class))
            )
    })
    @PostMapping("/voice-search")
    public ResponseEntity<AiParsedContactResponse> voiceSearch(@RequestParam String transcript) {
        AiParsedContactResponse response = contactAiService.parseVoiceSearchTranscript(transcript);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Generate smart notes for a contact")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Smart notes generated",
                    content = @Content(schema = @Schema(implementation = AiContactResponse.class))
            )
    })
    @PostMapping("/{contactId}/smart-notes")
    public ResponseEntity<AiContactResponse> smartNotes(@PathVariable String contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
        List<ContactInteraction> interactions = List.of();
        String notes = contactAiService.generateSmartNotes(
                new CMS.sys.dto.ContactResponseDTO(), interactions);
        return ResponseEntity.ok(AiContactResponse.builder()
                .response(notes)
                .model("qwen2.5-coder:1.5b")
                .durationMs(0)
                .build());
    }

    @Operation(summary = "Smart follow-up suggestions for a contact")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Follow-up suggestion generated",
                    content = @Content(schema = @Schema(implementation = AiFollowUpResponse.class))
            )
    })
    @PostMapping("/{contactId}/follow-up")
    public ResponseEntity<AiFollowUpResponse> followUp(@PathVariable String contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
        List<ContactInteraction> interactions = List.of();
        AiFollowUpResponse result = contactAiService.suggestFollowUp(contact, interactions);
        return ResponseEntity.ok(result);
    }
}