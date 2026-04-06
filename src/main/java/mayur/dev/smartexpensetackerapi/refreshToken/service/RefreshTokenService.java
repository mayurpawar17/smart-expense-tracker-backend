package mayur.dev.smartexpensetackerapi.refreshToken.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.refreshToken.entity.RefreshToken;
import mayur.dev.smartexpensetackerapi.refreshToken.repository.RefreshTokenRepository;
import mayur.dev.smartexpensetackerapi.user.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Delete old token if exists
        refreshTokenRepository.deleteByUser(user);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusDays(7));

        return refreshTokenRepository.save(token);
    }

    public RefreshToken verifyToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }
}