package mayur.dev.smartexpensetackerapi.features.auth.controller;

import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.core.utils.security.SecurityUtils;
import mayur.dev.smartexpensetackerapi.core.utils.dto.ApiResponse;
import mayur.dev.smartexpensetackerapi.features.auth.dto.AuthResponse;
import mayur.dev.smartexpensetackerapi.features.auth.dto.LoginRequest;
import mayur.dev.smartexpensetackerapi.features.auth.dto.RegisterRequest;
import mayur.dev.smartexpensetackerapi.features.auth.jwt.JwtUtil;
import mayur.dev.smartexpensetackerapi.features.auth.service.AuthService;
import mayur.dev.smartexpensetackerapi.features.refreshToken.dto.RefreshRequest;
import mayur.dev.smartexpensetackerapi.features.refreshToken.entity.RefreshToken;
import mayur.dev.smartexpensetackerapi.features.refreshToken.service.RefreshTokenService;
import mayur.dev.smartexpensetackerapi.features.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        AuthResponse data = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", data));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        AuthResponse data = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("User Login successfully", data));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUser() {
        User user = SecurityUtils.getCurrentUser();

        Map<String, Object> data = Map.of("id", user.getId(), "email", user.getEmail());
        return ResponseEntity.ok(ApiResponse.success("User Login successfully", data));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody RefreshRequest request) {

        RefreshToken refreshToken = refreshTokenService.verifyToken(request.getRefreshToken());

        String newAccessToken = jwtUtil.generateToken(refreshToken.getUser().getEmail());

        AuthResponse data = new AuthResponse(newAccessToken, refreshToken.getToken(), refreshToken.getUser().getEmail());

        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", data));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        User user = SecurityUtils.getCurrentUser();

        authService.logout(user);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Logged out successfully", null));
//        return ResponseEntity.ok("Logged out successfully");
    }
}