package com.proveperu.security;


import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.m06_usuarios.enums.EstadoUsuario;
import com.proveperu.m06_usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * CARGA USUARIO DESDE LA DB*/
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository
                .findByUsuarioLogin(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado: " + username
                        ));

        if (usuario.getEstadoFisico() != EstadoUsuario.ACTIVO) {
            throw new UsernameNotFoundException(
                    "Usuario suspendido"
            );
        }

        return User.builder()
                .username(usuario.getUsuarioLogin())
                .password(usuario.getPasswordHash())
                .authorities(buildAuthorities(usuario))
                .build();
    }

    private Collection<? extends GrantedAuthority>
    buildAuthorities(Usuario usuario) {

        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + usuario.getRol().getNombreRol()
                )
        );
    }
}
