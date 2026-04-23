package mayur.dev.smartexpensetackerapi.features.auth.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}