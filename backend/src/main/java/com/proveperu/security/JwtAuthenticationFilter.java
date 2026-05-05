package com.proveperu.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * INTERCEPTA PETICIONES */

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Extraer el header Authorization
        final String authHeader = request.getHeader("Authorization");

        // Si no hay token, continuar sin autenticar (el endpoint decidirá)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer el token y quitar "Bearer "
        final String jwt = authHeader.substring(7);
        try {
            // Extraer el username del token
            final String username = jwtService.extractUsername(jwt);

            // Si hay username y no hay autenticación activa en el contexto
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Cargar el usuario desde la base de datos
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validar que el token es correcto y no expiró
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Crear objeto de autenticación con los permisos del usuario
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // Registrar en el contexto de seguridad de Spring
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            }
        } catch (Exception e) {
            // Token inválido, expirado o malformado: NO SE AUTENTICA
            // El endpoint protegido devolverá 401 automáticamente
            SecurityContextHolder.clearContext();
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
