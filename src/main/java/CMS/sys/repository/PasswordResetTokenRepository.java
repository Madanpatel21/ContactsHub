package CMS.sys.repository;

import CMS.sys.entity.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUserIdAndUsedFalseAndExpiresAtAfter(String userId, LocalDateTime now);
}