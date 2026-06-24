package mayur.dev.smartexpensetackerapi.features.auth.service;

import mayur.dev.smartexpensetackerapi.features.auth.dto.AuthResponseDTO;
import mayur.dev.smartexpensetackerapi.features.auth.dto.LoginRequestDTO;
import mayur.dev.smartexpensetackerapi.features.auth.dto.RegisterRequestDTO;
import mayur.dev.smartexpensetackerapi.features.user.entity.User;

public interface AuthService {

    AuthResponseDTO register(RegisterRequestDTO request);

    AuthResponseDTO login(LoginRequestDTO request);

    void logout(User user);
}
