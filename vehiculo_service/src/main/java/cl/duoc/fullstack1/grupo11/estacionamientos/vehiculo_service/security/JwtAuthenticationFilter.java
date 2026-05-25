package cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());

        try {
            String email = jwtService.obtenerEmailDesdeToken(token);
            String rol = jwtService.obtenerRolDesdeToken(token);

            if (email != null && rol != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authenticationToken = construirAuthenticationToken(
                        email,
                        rol,
                        request
                );

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(securityContext);
            }

            filterChain.doFilter(request, response);

        } catch (JWTVerificationException ex) {
            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write("""
                    {
                      "status": 401,
                      "error": "Unauthorized",
                      "message": "Token JWT inválido o expirado"
                    }
                    """);
        }
    }

    private UsernamePasswordAuthenticationToken construirAuthenticationToken(
            String email,
            String rol,
            HttpServletRequest request
    ) {
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(rol)
        );

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, null, authorities);

        authenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        return authenticationToken;
    }
}