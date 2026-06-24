package mayur.dev.smartexpensetackerapi.features.auth.service;

import mayur.dev.smartexpensetackerapi.features.auth.entity.RefreshToken;
import mayur.dev.smartexpensetackerapi.features.auth.entity.User;

public interface RefreshTokenService {

    //Creates a new refresh token for a user, revoking any existing ones.
    RefreshToken createRefreshToken(User user);

    //Validates a refresh token's existence and checks if it has expired.
    RefreshToken verifyToken(String token);

    //Explicitly deletes/revokes all refresh tokens assigned to a user (e.g., during Logout).
    void deleteByUser(User user);
}
