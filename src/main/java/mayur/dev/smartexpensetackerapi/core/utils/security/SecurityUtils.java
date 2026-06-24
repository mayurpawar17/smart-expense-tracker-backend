package mayur.dev.smartexpensetackerapi.core.utils.security;

import lombok.extern.slf4j.Slf4j;
import mayur.dev.smartexpensetackerapi.core.exception.UnauthenticatedException;
import mayur.dev.smartexpensetackerapi.features.auth.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class to interact with Spring Security's thread-local storage context.
 */


/*
@Slf4j is a lombok annotation, It automatically generates a private static final Logger field (log) for this class at compile time. This allows you to write log.warn() or log.info() without manually declaring a logger instance.
 */
@Slf4j
public class SecurityUtils {

    //    public static User getCurrentUser() {
//        return (User) Objects.requireNonNull(
//                SecurityContextHolder.getContext().getAuthentication()
//        ).getPrincipal();
//    }
    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Defensive check: Verify a user token exists and matches our expected custom User instance
        if (auth != null && auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        }
        log.warn("No user found in security context");
        throw new UnauthenticatedException("No user found in security context");
    }
}
