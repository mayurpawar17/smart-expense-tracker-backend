package mayur.dev.smartexpensetackerapi.features.auth.service;


import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.features.auth.dto.AuthResponseDTO;
import mayur.dev.smartexpensetackerapi.features.auth.dto.LoginRequestDTO;
import mayur.dev.smartexpensetackerapi.features.auth.dto.RegisterRequestDTO;
import mayur.dev.smartexpensetackerapi.features.auth.jwt.JwtUtil;
import mayur.dev.smartexpensetackerapi.features.auth.entity.RefreshToken;
import mayur.dev.smartexpensetackerapi.features.auth.entity.User;
import mayur.dev.smartexpensetackerapi.features.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final RefreshTokenServiceImpl refreshTokenServiceImpl;

    @Override
    public AuthResponseDTO register(RegisterRequestDTO request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        // Generate access token
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getId());

        //Generate refresh token
        RefreshToken refreshToken = refreshTokenServiceImpl.createRefreshToken(user);

        return new AuthResponseDTO(accessToken, refreshToken.getToken(), user.getEmail());
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));

        //  Compare hashed password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getId());

        RefreshToken refreshToken = refreshTokenServiceImpl.createRefreshToken(user);

//        return new AuthResponse(token, user.getEmail());
        return new AuthResponseDTO(accessToken, refreshToken.getToken(), user.getEmail());
    }

    @Override
    public void logout(User user) {
        refreshTokenServiceImpl.deleteByUser(user);
    }


}