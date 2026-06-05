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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

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

        // Si llegamos aquí, las credenciales son correctas
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
