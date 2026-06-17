package cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.configuration;


import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String JEFE_SEGURIDAD = "Jefe_Seguridad";
    private static final String JEFE_SSDD = "Jefe_SSDD";
    private static final String GUARDIA = "Guardia";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // Registrar movimientos desde servicios operativos
                        .requestMatchers(HttpMethod.POST, "/api/v1/movimientos")
                        .hasAnyAuthority(JEFE_SEGURIDAD, JEFE_SSDD, GUARDIA)

                        // Listar todos los movimientos
                        .requestMatchers(HttpMethod.GET, "/api/v1/movimientos")
                        .hasAnyAuthority(JEFE_SEGURIDAD, JEFE_SSDD)

                        // Consultas específicas por ID, tipo, RUT, patente, plaza u origen
                        .requestMatchers(HttpMethod.GET, "/api/v1/movimientos/**")
                        .hasAnyAuthority(JEFE_SEGURIDAD, JEFE_SSDD, GUARDIA)

                        // Configuración Swagger
                        .requestMatchers("api/v1/public/**").permitAll()
                        .requestMatchers("/api/v1/public/**",
                        "/doc/swagger-ui.html",
                        "/doc/swagger-ui/index.html",
                        "/doc/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**")
                        .permitAll()

                        // Todo lo demás queda cerrado
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