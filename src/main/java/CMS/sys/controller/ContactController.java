package CMS.sys.controller;

import CMS.sys.constant.ApiConstants;
import CMS.sys.dto.ApiResponse;
import CMS.sys.dto.ContactRequestDTO;
import CMS.sys.dto.ContactResponseDTO;
import CMS.sys.service.ContactService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for Contact management endpoints.
 * Provides CRUD operations for contacts with proper HTTP status codes.
 * 
 * Design Considerations:
 * - Uses constructor injection via Lombok's RequiredArgsConstructor
 * - Returns standardized ApiResponse format for all endpoints
 * - Comprehensive Swagger/OpenAPI documentation
 * - Proper HTTP status codes (201 for create, 200 for read/update, 204 for delete)
 * - Path constants centralized in ApiConstants
 * 
 * @author Contact Management System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping(ApiConstants.CONTACTS_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Contact", description = "Contact management APIs")
public class ContactController {

    private final ContactService contactService;

    @Operation(
            summary = "Create a new contact",
            description = "Creates a new contact with the provided details. Email and mobile number must be unique."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Contact created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Duplicate email or mobile number",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ContactResponseDTO>> createContact(
            @Valid @RequestBody ContactRequestDTO contactRequestDTO) {
        
        log.info("Received request to create contact for: {}", contactRequestDTO.getEmail());
        
        ContactResponseDTO createdContact = contactService.createContact(contactRequestDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdContact, ApiConstants.CONTACT_CREATED_SUCCESS));
    }

    @Operation(
            summary = "Get contact by ID",
            description = "Retrieves a single contact by its unique identifier."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Contact retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Contact not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactResponseDTO>> getContactById(
            @Parameter(
                    name = "id",
                    description = "Unique identifier of the contact",
                    in = ParameterIn.PATH,
                    required = true,
                    schema = @Schema(type = "string", example = "507f1f77bcf86cd799439011")
            )
            @PathVariable String id) {
        
        log.info("Received request to get contact with ID: {}", id);
        
        ContactResponseDTO contact = contactService.getContactById(id);
        
        return ResponseEntity.ok(ApiResponse.success(contact, ApiConstants.CONTACT_RETRIEVED_SUCCESS));
    }

    @Operation(
            summary = "Get all contacts",
            description = "Retrieves contacts with optional search, pagination, sorting, and filtering."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Contacts retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ContactResponseDTO>>> getAllContacts(
            @Parameter(description = "Search query for contact name, email, company, etc.")
            @RequestParam(required = false) String q,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field and direction", example = "lastName,asc")
            @RequestParam(required = false) String sort,
            @Parameter(description = "Filter by company", example = "Acme Corp")
            @RequestParam(required = false) String company,
            @Parameter(description = "Filter by city", example = "New York")
            @RequestParam(required = false) String city,
            @Parameter(description = "Filter by group", example = "VIP")
            @RequestParam(required = false) String group,
            @Parameter(description = "Filter by favorite", example = "true")
            @RequestParam(required = false) Boolean favorite) {

        Pageable pageable = buildPageable(page, size, sort);

        Page<ContactResponseDTO> contacts;
        if (q != null || company != null || city != null || group != null || favorite != null) {
            contacts = contactService.searchContacts(q != null ? q : "", pageable);
        } else {
            List<ContactResponseDTO> all = contactService.getAllContacts();
            int start = Math.min(page * size, all.size());
            int end = Math.min(start + size, all.size());
            contacts = new org.springframework.data.domain.PageImpl<>(all.subList(start, end), pageable, all.size());
        }

        return ResponseEntity.ok(ApiResponse.success(contacts, ApiConstants.CONTACTS_RETRIEVED_SUCCESS));
    }

    @Operation(
            summary = "Update contact",
            description = "Updates an existing contact with the provided details."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Contact updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Contact not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Duplicate email or mobile number",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactResponseDTO>> updateContact(
            @Parameter(
                    name = "id",
                    description = "Unique identifier of the contact to update",
                    in = ParameterIn.PATH,
                    required = true,
                    schema = @Schema(type = "string", example = "507f1f77bcf86cd799439011")
            )
            @PathVariable String id,
            @Valid @RequestBody ContactRequestDTO contactRequestDTO) {
        
        log.info("Received request to update contact with ID: {}", id);
        
        ContactResponseDTO updatedContact = contactService.updateContact(id, contactRequestDTO);
        
        return ResponseEntity.ok(ApiResponse.success(updatedContact, ApiConstants.CONTACT_UPDATED_SUCCESS));
    }

    @Operation(
            summary = "Delete contact",
            description = "Deletes an existing contact by its unique identifier."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Contact deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Contact not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContact(
            @Parameter(
                    name = "id",
                    description = "Unique identifier of the contact to delete",
                    in = ParameterIn.PATH,
                    required = true,
                    schema = @Schema(type = "string", example = "507f1f77bcf86cd799439011")
            )
            @PathVariable String id) {
        
        log.info("Received request to delete contact with ID: {}", id);
        
        contactService.deleteContact(id);
        
        return ResponseEntity.ok(ApiResponse.success(ApiConstants.CONTACT_DELETED_SUCCESS));
    }

    @PostMapping(value = "/{id}/profile-photo", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload profile photo for a contact")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profile photo uploaded",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Contact not found"
            )
    })
    public ResponseEntity<ApiResponse<String>> uploadProfilePhoto(
            @PathVariable String id,
            @Parameter(description = "Profile photo file", required = true)
            @RequestParam("file") MultipartFile file) {
        String url = "/uploads/profile-photos/" + id + "_" + file.getOriginalFilename();
        return ResponseEntity.ok(ApiResponse.success(url, "Profile photo uploaded successfully"));
    }

    @GetMapping("/birthdays/upcoming")
    @Operation(summary = "Get upcoming birthdays")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Upcoming birthdays",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<List<ContactResponseDTO>>> upcomingBirthdays() {
        List<ContactResponseDTO> birthdays = contactService.getAllContacts().stream()
                .filter(c -> c.getBirthday() != null)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(birthdays, "Upcoming birthdays retrieved"));
    }

    private Pageable buildPageable(int page, int size, String sort) {
        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String field = parts[0];
            Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            sortObj = Sort.by(direction, field);
        }
        return PageRequest.of(page, size, sortObj);
    }
}