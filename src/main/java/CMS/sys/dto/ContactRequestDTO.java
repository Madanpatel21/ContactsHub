package CMS.sys.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Contact creation and update requests.
 * This DTO is used to receive contact data from API clients.
 * 
 * Design Considerations:
 * - Separates API contract from internal entity structure
 * - Includes comprehensive validation rules
 * - Uses Bean Validation annotations for input validation
 * - Swagger annotations for API documentation
 * - Immutable after validation (defensive programming)
 * 
 * @author Contact Management System
 * @version 1.0
 */
import java.time.LocalDate;
import java.util.List;

import CMS.sys.entity.PhoneNumber;
import CMS.sys.entity.EmailAddress;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating or updating a contact")
public class ContactRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(description = "First name of the contact", example = "John", required = true)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(description = "Last name of the contact", example = "Doe", required = true)
    private String lastName;

    @Size(max = 50, message = "Nickname must not exceed 50 characters")
    @Schema(description = "Nickname or preferred name", example = "Johnny")
    private String nickname;

    @Pattern(
        regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,9}$",
        message = "Invalid mobile number format"
    )
    @Schema(description = "Mobile phone number", example = "+1-234-567-8900")
    private String mobileNumber;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;

    @Size(max = 100, message = "Company name must not exceed 100 characters")
    @Schema(description = "Company or organization name", example = "Acme Corporation")
    private String company;

    @Size(max = 100, message = "Designation must not exceed 100 characters")
    @Schema(description = "Job title or designation", example = "Senior Software Engineer")
    private String designation;

    @Size(max = 200, message = "Address must not exceed 200 characters")
    @Schema(description = "Street address", example = "123 Main Street, Apt 4B")
    private String address;

    @Size(max = 50, message = "City name must not exceed 50 characters")
    @Schema(description = "City name", example = "New York")
    private String city;

    @Size(max = 50, message = "State name must not exceed 50 characters")
    @Schema(description = "State or province", example = "New York")
    private String state;

    @Size(max = 50, message = "Country name must not exceed 50 characters")
    @Schema(description = "Country name", example = "United States")
    private String country;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Schema(description = "Additional notes or comments", example = "Met at tech conference 2024")
    private String notes;

    @Schema(description = "Mark contact as favorite", example = "false", defaultValue = "false")
    private Boolean favorite;

    @Schema(description = "Latitude for geolocation", example = "40.7128")
    private Double latitude;

    @Schema(description = "Longitude for geolocation", example = "-74.0060")
    private Double longitude;

    @Schema(description = "Contact groups", example = "[\"Work\", \"VIP\"]")
    private java.util.List<String> groups;

    @Schema(description = "Multiple phone numbers", example = "[{\"number\":\"+1-234-567-8900\",\"type\":\"mobile\"}]")
    private List<PhoneNumber> phoneNumbers;

    @Schema(description = "Multiple email addresses", example = "[{\"email\":\"john@example.com\",\"type\":\"personal\"}]")
    private List<EmailAddress> emailAddresses;

    @Schema(description = "Birthday date", example = "1990-05-15")
    private java.time.LocalDate birthday;

    @Schema(description = "URL of uploaded profile photo")
    private String profilePhotoUrl;
}
