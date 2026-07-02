package CMS.sys.exception;

import CMS.sys.dto.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should handle validation errors and return field-level details")
    void handleMethodArgumentNotValid_ShouldReturnValidationErrors() {
        WebRequest request = mock(WebRequest.class);
        var target = new Object();
        var bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "email", "must not be blank"));
        bindingResult.addError(new FieldError("target", "mobileNumber", "Invalid format"));

        var exception = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                exception,
                HttpHeaders.EMPTY,
                HttpStatus.BAD_REQUEST,
                request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertNotNull(body);
        assertFalse(body.getSuccess());
        assertEquals("Validation failed", body.getMessage());
        GlobalExceptionHandler.ValidationErrorResponseWrapper wrapper = (GlobalExceptionHandler.ValidationErrorResponseWrapper) body.getData();
        assertNotNull(wrapper);
        assertEquals(2, wrapper.getErrors().size());
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException")
    void handleResourceNotFoundException_ShouldReturnNotFound() {
        WebRequest request = mock(WebRequest.class);
        var exception = new ResourceNotFoundException("Contact not found with id: 123");

        ResponseEntity<ApiResponse<Void>> response = handler.handleResourceNotFoundException(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.getSuccess());
        assertEquals("Contact not found with id: 123", body.getMessage());
    }

    @Test
    @DisplayName("Should handle DuplicateResourceException")
    void handleDuplicateResourceException_ShouldReturnConflict() {
        WebRequest request = mock(WebRequest.class);
        var exception = new DuplicateResourceException("Contact with this email already exists");

        ResponseEntity<ApiResponse<Void>> response = handler.handleDuplicateResourceException(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.getSuccess());
        assertEquals("Contact with this email already exists", body.getMessage());
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void handleGlobalException_ShouldReturnInternalServerError() {
        WebRequest request = mock(WebRequest.class);
        var exception = new RuntimeException("Unexpected error");

        ResponseEntity<ApiResponse<Void>> response = handler.handleGlobalException(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.getSuccess());
        assertEquals("An unexpected error occurred", body.getMessage());
    }
}