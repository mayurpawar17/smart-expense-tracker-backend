package mayur.dev.smartexpensetackerapi.auth.controller;

import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.auth.dto.AuthResponse;
import mayur.dev.smartexpensetackerapi.auth.dto.LoginRequest;
import mayur.dev.smartexpensetackerapi.auth.dto.RegisterRequest;
import mayur.dev.smartexpensetackerapi.auth.service.AuthService;
import mayur.dev.smartexpensetackerapi.core.utils.dto.ApiResponse;
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
}