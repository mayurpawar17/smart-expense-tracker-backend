package mayur.dev.smartexpensetackerapi.core.utils.security;

import mayur.dev.smartexpensetackerapi.features.user.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class SecurityUtils {

    public static User getCurrentUser() {
        return (User) Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()
        ).getPrincipal();
    }
}
