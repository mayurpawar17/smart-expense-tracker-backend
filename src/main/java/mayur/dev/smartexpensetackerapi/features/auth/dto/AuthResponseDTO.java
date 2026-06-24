package mayur.dev.smartexpensetackerapi.features.auth.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {

    private String accessToken;
    private String refreshToken;
    private String email;

    public AuthResponseDTO(String accessToken, String refreshToken, String email) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
    }

}