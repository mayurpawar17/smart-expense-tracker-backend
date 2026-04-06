package mayur.dev.smartexpensetackerapi.auth.dto;

import lombok.Data;
import mayur.dev.smartexpensetackerapi.user.entity.User;

@Data
public class AuthResponse {

    private String token;
    private String email;

    public AuthResponse(String token, String email) {
        this.token = token;
        this.email = email;
    }

    // getters
}