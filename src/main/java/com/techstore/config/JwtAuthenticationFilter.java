package com.techstore.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Obtener el header de autorización
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Chequeo rápido: ¿Tiene token? ¿Empieza por "Bearer "?
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Si no hay token, pasa la petición al siguiente filtro (Spring Security decidirá si lo rechaza)
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token (quitamos "Bearer " que son 7 caracteres)
        jwt = authHeader.substring(7);

        // 4. Extraer el email del token (usando tu JwtService de ayer)
        userEmail = jwtService.extractUsername(jwt);

        // 5. Validación de seguridad compleja
        // Si hay email Y el usuario no está autenticado todavía en el contexto...
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargamos los detalles del usuario desde la BD
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Verificamos si el token es válido matemáticamente y no ha expirado
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 6. Crear el objeto de autenticación (La "Credencial Verde")
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Añadimos detalles de la petición (IP, sesión, etc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. FINALMENTE: Autorizamos al usuario en el Contexto de Spring
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}