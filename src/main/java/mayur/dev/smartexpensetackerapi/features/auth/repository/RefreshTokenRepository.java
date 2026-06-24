package mayur.dev.smartexpensetackerapi.features.auth.repository;

import mayur.dev.smartexpensetackerapi.features.auth.entity.RefreshToken;
import mayur.dev.smartexpensetackerapi.features.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}