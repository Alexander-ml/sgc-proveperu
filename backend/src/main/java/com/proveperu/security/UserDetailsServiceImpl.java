package com.proveperu.security;


import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.m06_usuarios.enums.EstadoUsuario;
import com.proveperu.m06_usuarios.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación personalizada de {@link UserDetailsService} responsable de
 * integrar el modelo de usuarios de la aplicación con el mecanismo de
 * autenticación de Spring Security.
 *
 * <p>
 * Este componente actúa como puente entre la infraestructura de seguridad y
 * la capa de persistencia, permitiendo recuperar usuarios desde la base de
 * datos y transformarlos en objetos compatibles con el modelo de autenticación
 * utilizado por Spring Security.
 * </p>
 *
 * <p>
 * Sus responsabilidades principales incluyen:
 * </p>
 * <ul>
 *     <li>Localizar usuarios mediante su identificador de acceso.</li>
 *     <li>Validar que el usuario se encuentre habilitado para autenticarse.</li>
 *     <li>Construir instancias de {@link UserDetails} utilizadas durante el proceso de autenticación.</li>
 *     <li>Resolver las autoridades y roles asociados al usuario.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    /**
     * Repositorio encargado de acceder a la información de usuarios almacenada
     * en la base de datos.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Recupera la información de autenticación asociada a un usuario a partir
     * de su identificador de acceso.
     *
     * <p>
     * Este método es invocado automáticamente por Spring Security durante el
     * proceso de autenticación para obtener las credenciales y autoridades
     * necesarias para construir el contexto de seguridad.
     * </p>
     *
     * <p>
     * Adicionalmente, valida que el usuario se encuentre en estado activo antes
     * de permitir su autenticación en el sistema.
     * </p>
     *
     * @param username identificador de acceso utilizado para localizar al usuario.
     * @return instancia de {@link UserDetails} que representa al usuario autenticado.
     * @throws UsernameNotFoundException si el usuario no existe o no se encuentra
     *                                   habilitado para acceder al sistema.
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
log.info(
        "Buscando usuario para autenticación: {}",
        username
);
        Usuario usuario = usuarioRepository
                .findByUsuarioLogin(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado: " + username
                        ));
log.info(
        "Usuario encontrado: {}",
        usuario.getUsuarioLogin()
);
        if (usuario.getEstadoFisico() != EstadoUsuario.ACTIVO) {
                log.warn(
            "Intento de acceso con usuario suspendido: {}",
            username
    );
            throw new UsernameNotFoundException(
                    "Usuario suspendido"
            );
        }
log.info(
        "UserDetails construido para usuario: {}",
        username
);
        return User.builder()
                .username(usuario.getUsuarioLogin())
                .password(usuario.getPasswordHash())
                .authorities(buildAuthorities(usuario))
                .build();
    }

    /**
     * Construye el conjunto de autoridades de seguridad asociadas al usuario.
     *
     * <p>
     * Las autoridades generadas son utilizadas por Spring Security para
     * realizar controles de acceso basados en roles mediante mecanismos como
     * {@code @PreAuthorize}, expresiones de seguridad y reglas de autorización
     * configuradas en la aplicación.
     * </p>
     *
     * <p>
     * El rol funcional del usuario es transformado al formato estándar
     * {@code ROLE_*} requerido por Spring Security.
     * </p>
     *
     * @param usuario usuario del cual se obtendrán las autoridades.
     * @return colección de autoridades asignadas al usuario.
     */
    private Collection<? extends GrantedAuthority>
    buildAuthorities(Usuario usuario) {

        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + usuario.getRol().getNombreRol()
                )
        );
    }
}
