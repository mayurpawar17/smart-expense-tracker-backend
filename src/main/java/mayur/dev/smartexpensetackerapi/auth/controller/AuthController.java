package mayur.dev.smartexpensetackerapi.auth.controller;

import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.auth.dto.LoginRequest;
import mayur.dev.smartexpensetackerapi.auth.dto.RegisterRequest;
import mayur.dev.smartexpensetackerapi.auth.service.AuthService;
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
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}