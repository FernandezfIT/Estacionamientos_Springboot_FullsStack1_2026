package cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
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
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .anyRequest().denyAll()
                )

                .formLogin(form -> form.disable())

                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}
