package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.configuration;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // Endpoint interno usado por auth_service para validar login
                        .requestMatchers(HttpMethod.GET, "/api/v1/usuarios/auth/email/**").permitAll()

                        // Gestión de usuarios
                        .requestMatchers("/api/v1/usuarios/**")
                        .hasAnyAuthority("Jefe_Seguridad", "Jefe_SSDD")

                        // Catálogo de roles
                        .requestMatchers("/api/v1/roles/**")
                        .hasAnyAuthority("Jefe_Seguridad", "Jefe_SSDD")

                        // El Resto no pasa
                        .anyRequest().denyAll()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                escribirErrorJson(
                                        response,
                                        HttpServletResponse.SC_UNAUTHORIZED,
                                        "Unauthorized",
                                        "Token JWT requerido o inválido"
                                )
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                escribirErrorJson(
                                        response,
                                        HttpServletResponse.SC_FORBIDDEN,
                                        "Forbidden",
                                        "No tienes permisos para acceder a este recurso"
                                )
                        )
                )

                .formLogin(form -> form.disable())

                .httpBasic(httpBasic -> httpBasic.disable())

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void escribirErrorJson(
            HttpServletResponse response,
            int status,
            String error,
            String message
    ) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
                {
                  "status": %d,
                  "error": "%s",
                  "message": "%s"
                }
                """.formatted(status, error, message));
    }
}