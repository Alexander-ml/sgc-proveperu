package com.proveperu.auth.service;

import com.proveperu.auth.dto.request.LoginRequest;
import com.proveperu.auth.dto.response.LoginResponse;
import com.proveperu.security.JwtService;
import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.m06_usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación responsable de gestionar el proceso de autenticación
 * de usuarios dentro del módulo de seguridad.
 *
 * <p>
 * Su responsabilidad principal consiste en validar las credenciales recibidas,
 * recuperar la información del usuario autenticado, generar el token JWT de acceso
 * y construir la respuesta de autenticación que será consumida por los clientes
 * de la API.
 * </p>
 *
 * <p>
 * Este servicio actúa como punto de orquestación entre Spring Security,
 * la capa de persistencia de usuarios y el mecanismo de generación de tokens,
 * manteniendo desacoplada la lógica de autenticación de los controladores.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    /**
     * Componente encargado de delegar el proceso de autenticación
     * a la infraestructura de Spring Security.
     */
    private final AuthenticationManager authenticationManager;
    /**
     * Repositorio utilizado para recuperar la información del usuario
     * autenticado desde la base de datos.
     */
    private final UsuarioRepository usuarioRepository;
    /**
     * Servicio responsable de la generación y administración
     * de tokens JWT utilizados para la autenticación stateless.
     */
    private final JwtService jwtService;

    /**
     * Autentica un usuario utilizando sus credenciales de acceso,
     * genera un token JWT y construye la respuesta de autenticación.
     *
     * <p>
     * El flujo ejecutado por este método comprende:
     * </p>
     * <ul>
     *     <li>Validación de credenciales mediante Spring Security.</li>
     *     <li>Obtención de la información completa del usuario.</li>
     *     <li>Construcción del objeto {@link UserDetails} requerido para el JWT.</li>
     *     <li>Generación del token de acceso con información del rol.</li>
     *     <li>Creación de la respuesta de autenticación para el cliente.</li>
     * </ul>
     *
     * @param request objeto que contiene las credenciales de acceso
     *                proporcionadas por el usuario.
     * @return respuesta de autenticación que incluye el token JWT,
     *         tipo de autenticación, información básica del usuario,
     *         rol asignado y tiempo de expiración del token.
     *
     * @throws org.springframework.security.authentication.BadCredentialsException
     * si las credenciales proporcionadas son inválidas.
     * @throws UsernameNotFoundException
     * si el usuario autenticado no puede ser recuperado desde la base de datos.
     */
    public LoginResponse login(LoginRequest request) {

        // Delegar autenticación a Spring Security
        // verifica credenciales con BCrypt automáticamente
        // lanza BadCredentialsException si son incorrectas
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsuarioLogin(),
                        request.getPassword()
                )
        );

        // Cargar el usuario completo para construir la respuesta
        Usuario usuario = usuarioRepository
                .findByUsuarioLogin(request.getUsuarioLogin())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado: " + request.getUsuarioLogin()
                        ));

        // Construcción local del UserDetails
        UserDetails userDetails =
                User.builder()
                        .username(usuario.getUsuarioLogin())
                        .password(usuario.getPasswordHash())
                        .authorities("ROLE_" + usuario.getRol().getNombreRol())
                        .build();

        // Generar el JWT con el rol incluido
        String token = jwtService.generateToken(
                userDetails,
                usuario.getRol().getNombreRol()
        );

        // Construir y devolver la respuesta
        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .usuarioLogin(usuario.getUsuarioLogin())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol().getNombreRol())
                .expiresIn(jwtService.getExpirationMs())
                .build();
    }
}
