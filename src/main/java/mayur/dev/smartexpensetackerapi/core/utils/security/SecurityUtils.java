package mayur.dev.smartexpensetackerapi.core.utils.security;

import mayur.dev.smartexpensetackerapi.core.exception.UnauthenticatedException;
import mayur.dev.smartexpensetackerapi.features.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class SecurityUtils {

    //    public static User getCurrentUser() {
//        return (User) Objects.requireNonNull(
//                SecurityContextHolder.getContext().getAuthentication()
//        ).getPrincipal();
//    }
    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        }
        throw new UnauthenticatedException("No user found in security context");
    }
}
