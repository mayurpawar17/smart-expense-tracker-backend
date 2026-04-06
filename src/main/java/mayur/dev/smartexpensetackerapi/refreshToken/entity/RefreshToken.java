package mayur.dev.smartexpensetackerapi.refreshToken.entity;

import jakarta.persistence.*;
import lombok.Data;
import mayur.dev.smartexpensetackerapi.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Data
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private LocalDateTime expiryDate;

    @ManyToOne
    private User user;

}