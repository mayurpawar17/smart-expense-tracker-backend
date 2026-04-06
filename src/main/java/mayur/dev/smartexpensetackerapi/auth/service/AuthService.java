package mayur.dev.smartexpensetackerapi.auth.service;


import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.auth.dto.LoginRequest;
import mayur.dev.smartexpensetackerapi.auth.dto.RegisterRequest;
import mayur.dev.smartexpensetackerapi.auth.jwt.JwtUtil;
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


    // Register
    public String register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email already exists";
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Hash password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return "User registered successfully";
    }

    // Login
    public String login(LoginRequest request) {

        User user = (User) userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));

        //  Compare hashed password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        // Generate JWT
        return jwtUtil.generateToken(user.getEmail());
    }


}