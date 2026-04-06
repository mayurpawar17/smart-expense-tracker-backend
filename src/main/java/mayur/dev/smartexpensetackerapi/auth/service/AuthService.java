package mayur.dev.smartexpensetackerapi.auth.service;


import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.auth.dto.AuthResponse;
import mayur.dev.smartexpensetackerapi.auth.dto.LoginRequest;
import mayur.dev.smartexpensetackerapi.auth.dto.RegisterRequest;
import mayur.dev.smartexpensetackerapi.auth.jwt.JwtUtil;
import mayur.dev.smartexpensetackerapi.refreshToken.entity.RefreshToken;
import mayur.dev.smartexpensetackerapi.refreshToken.repository.RefreshTokenRepository;
import mayur.dev.smartexpensetackerapi.refreshToken.service.RefreshTokenService;
import mayur.dev.smartexpensetackerapi.user.entity.User;
import mayur.dev.smartexpensetackerapi.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private  final RefreshTokenService refreshTokenService;


    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        // Generate access token
        String accessToken = jwtUtil.generateToken(user.getEmail());

        //Generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                user.getEmail()
        );
    }

    // Login
    public AuthResponse  login(LoginRequest request) {

        User user = (User) userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));

        //  Compare hashed password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String accessToken = jwtUtil.generateToken(user.getEmail());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

//        return new AuthResponse(token, user.getEmail());
        return new AuthResponse(accessToken, refreshToken.getToken(), user.getEmail());
    }


}