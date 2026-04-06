package mayur.dev.smartexpensetackerapi.refreshToken.repository;

import mayur.dev.smartexpensetackerapi.refreshToken.entity.RefreshToken;
import mayur.dev.smartexpensetackerapi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}