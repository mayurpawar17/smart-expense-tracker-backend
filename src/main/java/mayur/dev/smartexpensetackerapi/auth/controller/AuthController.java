package mayur.dev.smartexpensetackerapi.auth.controller;

import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.auth.dto.AuthResponse;
import mayur.dev.smartexpensetackerapi.auth.dto.LoginRequest;
import mayur.dev.smartexpensetackerapi.auth.dto.RegisterRequest;
import mayur.dev.smartexpensetackerapi.auth.jwt.JwtUtil;
import mayur.dev.smartexpensetackerapi.auth.service.AuthService;
import mayur.dev.smartexpensetackerapi.core.utils.dto.ApiResponse;
import mayur.dev.smartexpensetackerapi.refreshToken.dto.RefreshRequest;
import mayur.dev.smartexpensetackerapi.refreshToken.entity.RefreshToken;
import mayur.dev.smartexpensetackerapi.refreshToken.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "User Login successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody RefreshRequest request) {

        RefreshToken refreshToken = refreshTokenService.verifyToken(request.getRefreshToken());

        String newAccessToken = jwtUtil.generateToken(refreshToken.getUser().getEmail());

        AuthResponse response = new AuthResponse(newAccessToken, refreshToken.getToken(), refreshToken.getUser().getEmail());

        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
    }
}