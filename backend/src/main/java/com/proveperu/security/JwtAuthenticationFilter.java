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
 * Filtro de seguridad responsable de procesar y validar los tokens JWT
 * presentes en las solicitudes HTTP entrantes.
 *
 * <p>
 * Este componente forma parte de la infraestructura de seguridad de la aplicación
 * y se ejecuta una única vez por cada petición gracias a la herencia de
 * {@link OncePerRequestFilter}.
 * </p>
 *
 * <p>
 * Su responsabilidad principal consiste en:
 * </p>
 * <ul>
 *     <li>Interceptar las solicitudes HTTP antes de que alcancen los controladores.</li>
 *     <li>Extraer el token JWT desde la cabecera Authorization.</li>
 *     <li>Validar la integridad y vigencia del token.</li>
 *     <li>Recuperar la información del usuario autenticado.</li>
 *     <li>Registrar la autenticación en el contexto de seguridad de Spring.</li>
 * </ul>
 *
 * <p>
 * Este mecanismo permite implementar una estrategia de autenticación stateless
 * basada en JWT, evitando el uso de sesiones HTTP en el servidor.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Servicio encargado de extraer, validar y procesar la información
     * contenida en los tokens JWT.
     */
    private final JwtService jwtService;
    /**
     * Servicio responsable de recuperar los datos de autenticación y autorización
     * de los usuarios desde la fuente de datos configurada.
     */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Procesa la solicitud HTTP actual para determinar si contiene un token JWT válido
     * y, en caso afirmativo, establece la autenticación correspondiente dentro del
     * contexto de seguridad de Spring.
     *
     * <p>
     * El flujo de procesamiento contempla las siguientes etapas:
     * </p>
     * <ul>
     *     <li>Obtención de la cabecera Authorization.</li>
     *     <li>Extracción del token JWT.</li>
     *     <li>Obtención del identificador del usuario desde el token.</li>
     *     <li>Recuperación de la información de seguridad del usuario.</li>
     *     <li>Validación de vigencia e integridad del token.</li>
     *     <li>Registro de la autenticación en el SecurityContext.</li>
     * </ul>
     *
     * <p>
     * Si el token es inválido, expiró o presenta un formato incorrecto,
     * la autenticación no será establecida y la solicitud continuará su flujo
     * normal. Los endpoints protegidos serán responsables de rechazar el acceso
     * mediante una respuesta HTTP 401 cuando corresponda.
     * </p>
     *
     * @param request solicitud HTTP recibida por la aplicación.
     * @param response respuesta HTTP asociada a la solicitud.
     * @param filterChain cadena de filtros de Spring Security que continuará
     *                    procesando la petición.
     *
     * @throws ServletException si ocurre un error durante el procesamiento
     *                          del filtro.
     * @throws IOException si ocurre un error de entrada o salida durante
     *                     el procesamiento de la solicitud.
     */
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
