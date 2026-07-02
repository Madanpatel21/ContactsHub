package CMS.sys.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import CMS.sys.entity.PhoneNumber;
import CMS.sys.entity.EmailAddress;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing contact details")
public class ContactResponseDTO {

    @Schema(description = "Unique identifier of the contact", example = "507f1f77bcf86cd799439011")
    private String id;

    @Schema(description = "First name of the contact", example = "John")
    private String firstName;

    @Schema(description = "Last name of the contact", example = "Doe")
    private String lastName;

    @Schema(description = "Nickname or preferred name", example = "Johnny")
    private String nickname;

    @Schema(description = "Mobile phone number", example = "+1-234-567-8900")
    private String mobileNumber;

    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Company or organization name", example = "Acme Corporation")
    private String company;

    @Schema(description = "Job title or designation", example = "Senior Software Engineer")
    private String designation;

    @Schema(description = "Street address", example = "123 Main Street, Apt 4B")
    private String address;

    @Schema(description = "City name", example = "New York")
    private String city;

    @Schema(description = "State or province", example = "New York")
    private String state;

    @Schema(description = "Country name", example = "United States")
    private String country;

    @Schema(description = "Additional notes or comments", example = "Met at tech conference 2024")
    private String notes;

    @Schema(description = "Indicates if contact is marked as favorite", example = "false")
    private Boolean favorite;

    @Schema(description = "Latitude for geolocation", example = "40.7128")
    private Double latitude;

    @Schema(description = "Longitude for geolocation", example = "-74.0060")
    private Double longitude;

    @Schema(description = "Last contacted date", example = "2024-01-15T14:30:00")
    private LocalDateTime lastContactedDate;

    @Schema(description = "Total interaction count for this contact", example = "5")
    private Long interactionCount;

    @Schema(description = "Contact groups", example = "[\"Work\", \"VIP\"]")
    private java.util.List<String> groups;

    @Schema(description = "Multiple phone numbers")
    private List<PhoneNumber> phoneNumbers;

    @Schema(description = "Multiple email addresses")
    private List<EmailAddress> emailAddresses;

    @Schema(description = "URL of profile photo", example = "/uploads/profile-photos/contact-123.jpg")
    private String profilePhotoUrl;

    @Schema(description = "Birthday date", example = "1990-05-15")
    private java.time.LocalDate birthday;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Timestamp when the contact was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Timestamp when the contact was last updated", example = "2024-01-15T14:45:00")
    private LocalDateTime updatedAt;

    // Future computed fields that can be added:
    // - private String fullName; // Computed from firstName + lastName
    // - private String initials; // Computed from firstName + lastName
    // - private Integer daysUntilBirthday; // Computed from birthday field
    // - private String displayName; // nickname if present, else fullName
}
