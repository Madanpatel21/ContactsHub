package CMS.sys.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication request payload")
public class AuthRequest {

    @Schema(description = "Username for authentication", example = "john.doe")
    private String username;

    @Schema(description = "Password for authentication", example = "password123")
    private String password;
}