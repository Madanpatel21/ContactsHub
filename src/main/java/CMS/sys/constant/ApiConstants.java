package CMS.sys.constant;

/**
 * Contains all API-related constants used throughout the application.
 * This class centralizes API paths, versions, and other API-related configuration.
 * 
 * @author Contact Management System
 * @version 1.0
 */
public final class ApiConstants {

    private ApiConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // API Version
    public static final String API_VERSION = "/api/v1";

    // Base Paths
    public static final String CONTACTS_BASE_PATH = API_VERSION + "/contacts";
    public static final String ANALYTICS_BASE_PATH = API_VERSION + "/analytics";

    // Success Messages
    public static final String CONTACT_CREATED_SUCCESS = "Contact created successfully";
    public static final String CONTACT_UPDATED_SUCCESS = "Contact updated successfully";
    public static final String CONTACT_DELETED_SUCCESS = "Contact deleted successfully";
    public static final String CONTACT_RETRIEVED_SUCCESS = "Contact retrieved successfully";
    public static final String CONTACTS_RETRIEVED_SUCCESS = "Contacts retrieved successfully";

    // Error Messages
    public static final String CONTACT_NOT_FOUND = "Contact not found with id: ";
    public static final String INVALID_INPUT = "Invalid input provided";
    public static final String DUPLICATE_EMAIL = "Contact with this email already exists";
    public static final String DUPLICATE_MOBILE = "Contact with this mobile number already exists";
}
