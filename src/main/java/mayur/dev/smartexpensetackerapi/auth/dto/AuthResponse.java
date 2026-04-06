package mayur.dev.smartexpensetackerapi.auth.dto;

import lombok.Data;
import mayur.dev.smartexpensetackerapi.user.entity.User;

@Data
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String email;

    public AuthResponse(String accessToken, String refreshToken, String email) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
    }

}