package CMS.sys.repository;

import CMS.sys.entity.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUserIdAndRevokedFalse(String userId);

    List<RefreshToken> findByUserId(String userId);
}