package mayur.dev.smartexpensetackerapi.core.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.auth.jwt.JwtAuthFilter;
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
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth.requestMatchers("/v1" +
                                "/auth/**").permitAll()
                        .anyRequest().authenticated()
                )


                //THIS FIXES 401 vs 403 ISSUE
                .exceptionHandling(ex -> ex
                                .authenticationEntryPoint((request, response, authException) -> {
//                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json");

                                    ApiResponse<Object> apiResponse =
                                            ApiResponse.error("Please login to continue");

                                    response.getWriter().write(mapper.writeValueAsString(apiResponse));
                                }).accessDeniedHandler((request, response, accessDeniedException) -> {

                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    response.setContentType("application/json");

                                    ApiResponse<Object> apiResponse = ApiResponse.error("Access denied");

                                    response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
                                })
                )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}