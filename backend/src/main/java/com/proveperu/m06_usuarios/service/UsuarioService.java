package com.proveperu.m06_usuarios.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.proveperu.m06_usuarios.dto.response.UsuarioDashboardResponse;
import com.proveperu.m06_usuarios.dto.response.UsuarioListadoResponse;
import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.m06_usuarios.enums.EstadoUsuario;
import com.proveperu.m06_usuarios.repository.RolRepository;
import com.proveperu.m06_usuarios.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.proveperu.m06_usuarios.dto.request.CrearUsuarioRequest;
import com.proveperu.m06_usuarios.dto.request.EditarUsuarioRequest;
import com.proveperu.m06_usuarios.entity.Rol;
import lombok.RequiredArgsConstructor;

import com.proveperu.m06_usuarios.dto.response.UsuarioDetalleResponse;
/**
 * Servicio encargado de gestionar las operaciones
 * relacionadas con usuarios y roles.
 *
 * Proporciona información consolidada para el dashboard
 * del módulo de usuarios.
 *
 * @author David Sanchez
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {
    
     /**
     * Repositorio de usuarios.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Repositorio de roles.
     */
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Obtiene los indicadores principales del dashboard
     * de usuarios.
     *
     * @return información consolidada del dashboard.
     */
    public UsuarioDashboardResponse obtenerDashboard() {

        Long totalUsuarios = usuarioRepository.count();

        Long usuariosActivos =
                usuarioRepository.countByEstadoFisico(
                        EstadoUsuario.ACTIVO
                );

        Long usuariosSuspendidos =
                usuarioRepository.countByEstadoFisico(
                        EstadoUsuario.SUSPENDIDO
                );

        Long rolesDefinidos =
                rolRepository.count();

        return UsuarioDashboardResponse.builder()
                .totalUsuarios(totalUsuarios)
                .usuariosActivos(usuariosActivos)
                .usuariosSuspendidos(usuariosSuspendidos)
                .rolesDefinidos(rolesDefinidos)
                .build();
            }
    /**
     * Obtiene la lista de usuarios registrados
     * en el sistema.
     *
     * @param nombre filtro opcional por nombre.
     * @return lista de usuarios.
     */
    public List<UsuarioListadoResponse> listarUsuarios(String nombre) {

        List<Usuario> usuarios;

        if (nombre == null || nombre.isBlank()) {
            usuarios = usuarioRepository.findByNombreCompletoContainingIgnoreCase("");
        } else {
            usuarios = usuarioRepository.findByNombreCompletoContainingIgnoreCase(nombre);
        }

        return usuarios.stream()
                .map(usuario -> UsuarioListadoResponse.builder()
                        .idUsuario(usuario.getIdUsuario())
                        .nombreCompleto(usuario.getNombreCompleto())
                        .usuarioLogin(usuario.getUsuarioLogin())
                        .rol(usuario.getRol().getNombreRol())
                        .estado(usuario.getEstadoFisico().name())
                        .build())
                .collect(Collectors.toList());
    }
    public void crearUsuario(CrearUsuarioRequest request) {

        Rol rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() ->
                        new RuntimeException("Rol no encontrado")
                );

        Usuario usuario = Usuario.builder()
                .nombreCompleto(request.getNombreCompleto())
                .usuarioLogin(request.getUsuarioLogin())
                .passwordHash(
                        passwordEncoder.encode(request.getPassword())
                )
                .rol(rol)
                .estadoFisico(EstadoUsuario.ACTIVO)
                .build();

        usuarioRepository.save(usuario);
    }
    public void editarUsuario(
            Integer idUsuario,
            EditarUsuarioRequest request
    ) {

        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no encontrado"
                        )
                );

        Rol rol = rolRepository
                .findById(request.getIdRol())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Rol no encontrado"
                        )
                );

        usuario.setNombreCompleto(
                request.getNombreCompleto()
        );

        usuario.setUsuarioLogin(
                request.getUsuarioLogin()
        );

        usuario.setRol(rol);

        usuario.setEstadoFisico(
                EstadoUsuario.valueOf(
                        request.getEstado().toUpperCase()
                )
        );

        usuarioRepository.save(usuario);
    }
    public UsuarioDetalleResponse obtenerUsuarioPorId(Integer id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no encontrado"
                        ));

        return UsuarioDetalleResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombreCompleto(usuario.getNombreCompleto())
                .usuarioLogin(usuario.getUsuarioLogin())
                .idRol(usuario.getRol().getIdRol())
                .rol(usuario.getRol().getNombreRol())
                .estado(usuario.getEstadoFisico().name())
                .build();
    }
    public void suspenderUsuario(Integer idUsuario) {

        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no encontrado"
                        )
                );

        usuario.setEstadoFisico(
                EstadoUsuario.SUSPENDIDO
        );

        usuarioRepository.save(usuario);
    }
    public void activarUsuario(Integer idUsuario) {

        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no encontrado"
                        )
                );

        usuario.setEstadoFisico(
                EstadoUsuario.ACTIVO
        );

        usuarioRepository.save(usuario);
    } 
}
