package mayur.dev.smartexpensetackerapi.features.auth.controller;

import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.core.utils.security.SecurityUtils;
import mayur.dev.smartexpensetackerapi.core.utils.dto.ApiResponse;
import mayur.dev.smartexpensetackerapi.features.auth.dto.AuthResponseDTO;
import mayur.dev.smartexpensetackerapi.features.auth.dto.LoginRequestDTO;
import mayur.dev.smartexpensetackerapi.features.auth.dto.RegisterRequestDTO;
import mayur.dev.smartexpensetackerapi.features.auth.jwt.JwtUtil;
import mayur.dev.smartexpensetackerapi.features.auth.service.AuthServiceImpl;
import mayur.dev.smartexpensetackerapi.features.auth.dto.RefreshRequestDTO;
import mayur.dev.smartexpensetackerapi.features.auth.entity.RefreshToken;
import mayur.dev.smartexpensetackerapi.features.auth.service.RefreshTokenService;
import mayur.dev.smartexpensetackerapi.features.auth.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authServiceImpl;
    private final JwtUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(@RequestBody RegisterRequestDTO request) {
        AuthResponseDTO data = authServiceImpl.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", data));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@RequestBody LoginRequestDTO request) {
        AuthResponseDTO data = authServiceImpl.login(request);
        return ResponseEntity.ok(ApiResponse.success("User Login successfully", data));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUser() {
        User user = SecurityUtils.getCurrentUser();

        Map<String, Object> data = Map.of("id", user.getId(), "email", user.getEmail());
        return ResponseEntity.ok(ApiResponse.success("User Login successfully", data));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> refresh(@RequestBody RefreshRequestDTO request) {

        RefreshToken refreshToken = refreshTokenService.verifyToken(request.getRefreshToken());

        String newAccessToken = jwtUtil.generateToken(refreshToken.getUser().getEmail(), refreshToken.getUser().getId());

        AuthResponseDTO data = new AuthResponseDTO(newAccessToken, refreshToken.getToken(), refreshToken.getUser().getEmail());

        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", data));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        User user = SecurityUtils.getCurrentUser();

        authServiceImpl.logout(user);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Logged out successfully", null));
//        return ResponseEntity.ok("Logged out successfully");
    }
}