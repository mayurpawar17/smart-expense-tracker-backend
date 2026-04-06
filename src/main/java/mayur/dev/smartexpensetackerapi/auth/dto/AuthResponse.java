package mayur.dev.smartexpensetackerapi.auth.dto;

import mayur.dev.smartexpensetackerapi.user.entity.User;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String tokenType,
        User user
) {}