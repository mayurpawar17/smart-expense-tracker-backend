package mayur.dev.smartexpensetackerapi.core.utils.security;

import lombok.extern.slf4j.Slf4j;
import mayur.dev.smartexpensetackerapi.core.exception.UnauthenticatedException;
import mayur.dev.smartexpensetackerapi.features.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

@Slf4j
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
        log.warn("No user found in security context");
        throw new UnauthenticatedException("No user found in security context");
    }
}
