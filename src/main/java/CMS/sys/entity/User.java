package CMS.sys.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String role;

    private boolean enabled = true;

    private boolean accountNonLocked = true;

    private int failedLoginAttempts;

    private LocalDateTime lockoutUntil;

    private String passwordResetToken;

    private LocalDateTime passwordResetTokenExpiry;

    private String emailVerificationToken;

    private boolean emailVerified = false;

    private LocalDateTime lastLoginAt;
}