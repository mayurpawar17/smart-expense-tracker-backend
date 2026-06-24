package mayur.dev.smartexpensetackerapi.features.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequestDTO {
    private String refreshToken;
}