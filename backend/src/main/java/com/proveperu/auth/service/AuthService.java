package com.proveperu.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proveperu.auth.dto.request.LoginRequest;
import com.proveperu.auth.dto.response.LoginResponse;
import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.m06_usuarios.entity.UsuarioSesion;
import com.proveperu.m06_usuarios.repository.UsuarioRepository;
import com.proveperu.m06_usuarios.repository.UsuarioSesionRepository;
import com.proveperu.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
 * Repositorio utilizado para registrar las sesiones
 * iniciadas por los usuarios.
 */
private final UsuarioSesionRepository usuarioSesionRepository;
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
        log.info(
        "Intento de autenticación para el usuario: {}",
        request.getUsuarioLogin()
);
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
        log.info(
        "Usuario autenticado correctamente: {}",
        usuario.getUsuarioLogin()
);
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
        UsuarioSesion sesion = UsuarioSesion.builder()
        .usuario(usuario)
        .fechaHoraInicio(LocalDateTime.now())
        .fechaHoraFin(null)
        .build();

usuarioSesionRepository.save(sesion);

log.info(
        "Sesión registrada correctamente para el usuario: {}",
        usuario.getUsuarioLogin()
);
log.info(
        "JWT generado para usuario: {} con rol {}",
        usuario.getUsuarioLogin(),
        usuario.getRol().getNombreRol()
);

log.info(
        "Login completado para usuario: {}",
        usuario.getUsuarioLogin()
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

    /**
 * Registra el cierre de la última sesión abierta
 * correspondiente al usuario autenticado.
 *
 * @param usuarioLogin correo del usuario autenticado.
 */
@Transactional
public void logout(String usuarioLogin) {

    log.info(
            "Intento de cierre de sesión para el usuario: {}",
            usuarioLogin
    );

    Usuario usuario = usuarioRepository
            .findByUsuarioLogin(usuarioLogin)
            .orElseThrow(() -> {
                log.warn(
                        "No se encontró al usuario durante el cierre de sesión: {}",
                        usuarioLogin
                );

                return new UsernameNotFoundException(
                        "Usuario no encontrado: " + usuarioLogin
                );
            });

    UsuarioSesion sesion = usuarioSesionRepository
            .findFirstByUsuarioAndFechaHoraFinIsNullOrderByFechaHoraInicioDesc(
                    usuario
            )
            .orElseThrow(() -> {
                log.warn(
                        "El usuario {} no tiene una sesión abierta",
                        usuarioLogin
                );

                return new RuntimeException(
                        "No existe una sesión activa para el usuario"
                );
            });

    sesion.setFechaHoraFin(LocalDateTime.now());

    usuarioSesionRepository.save(sesion);

    log.info(
            "Cierre de sesión registrado correctamente para el usuario: {}",
            usuarioLogin
    );
}
}
