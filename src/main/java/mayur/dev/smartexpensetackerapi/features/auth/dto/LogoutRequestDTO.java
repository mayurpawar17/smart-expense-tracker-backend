package mayur.dev.smartexpensetackerapi.features.auth.dto;

import lombok.Data;

@Data
public class LogoutRequestDTO {
    private String refreshToken;
}