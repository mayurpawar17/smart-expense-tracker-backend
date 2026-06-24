package mayur.dev.smartexpensetackerapi.core.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.features.auth.jwt.JwtAuthFilter;
import mayur.dev.smartexpensetackerapi.core.utils.dto.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        //Disable CSRF because JWT-based APIs do not use browser sessions/cookies
        http.csrf(AbstractHttpConfigurer::disable);

        //Make the application stateless. No server-side sessions will store user state.
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //Define the URL access permissions (The Gatekeeper Rules)
        //Allow anyone to access authentication routes (login, register, forgot password)
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/api/v1/auth/**").permitAll().requestMatchers("/actuator/health").permitAll().anyRequest().authenticated());

        //Tell Spring how to respond when security checks fail
        //Triggers when a user tries to access a secured route without logging in (401)
        //Triggers when a logged-in user tries to access a route they don't have permission for (403)
        http.exceptionHandling(exception -> exception.authenticationEntryPoint((request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            ApiResponse<Object> apiResponse = ApiResponse.error("Please login to continue");
            response.getWriter().write(mapper.writeValueAsString(apiResponse));
        }).accessDeniedHandler((request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            ApiResponse<Object> apiResponse = ApiResponse.error("Access denied");
            response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
        }));

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}