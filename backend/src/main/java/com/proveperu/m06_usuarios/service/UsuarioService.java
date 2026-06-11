package com.proveperu.m06_usuarios.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proveperu.m06_usuarios.dto.request.CrearUsuarioRequest;
import com.proveperu.m06_usuarios.dto.request.EditarUsuarioRequest;
import com.proveperu.m06_usuarios.dto.response.UsuarioDashboardResponse;
import com.proveperu.m06_usuarios.dto.response.UsuarioDetalleResponse;
import com.proveperu.m06_usuarios.dto.response.UsuarioListadoResponse;
import com.proveperu.m06_usuarios.entity.Rol;
import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.m06_usuarios.enums.EstadoUsuario;
import com.proveperu.m06_usuarios.repository.RolRepository;
import com.proveperu.m06_usuarios.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
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
         log.info("Consultando dashboard de usuarios");

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

    log.info("Listando usuarios. Filtro nombre: {}", nombre);
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
log.info(
        "Creando usuario con login {}",
        request.getUsuarioLogin()
);
        Rol rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() ->{

            log.warn(
                    "No se encontró el rol con id {} para crear usuario",
                    request.getIdRol()
            );

            return new RuntimeException(
                    "Rol no encontrado"
            );
        });

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
        log.info(
        "Usuario {} creado correctamente",
        request.getUsuarioLogin()
);
    }
    
    public void editarUsuario(
            Integer idUsuario,
            EditarUsuarioRequest request
    ) {
log.info(
        "Editando usuario con id {}",
        idUsuario
);
        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() -> {

            log.warn(
                    "No se encontró el usuario con id {} para editar",
                    idUsuario
            );

            return new RuntimeException(
                    "Usuario no encontrado"
            );
        });

        Rol rol = rolRepository
                .findById(request.getIdRol())
                .orElseThrow(() ->{

            log.warn(
                    "No se encontró el rol con id {} para editar usuario",
                    request.getIdRol()
            );

            return new RuntimeException(
                    "Rol no encontrado"
            );
        } );

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
        log.info(
        "Usuario {} actualizado correctamente",
        idUsuario
);
    }
    public UsuarioDetalleResponse obtenerUsuarioPorId(Integer id) {
log.info(
        "Consultando usuario con id {}",
        id
);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {

            log.warn(
                    "No se encontró el usuario con id {}",
                    id
            );

            return new RuntimeException(
                    "Usuario no encontrado"
            );
        });

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
log.info(
        "Suspendiendo usuario con id {}",
        idUsuario
);
        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->{

            log.warn(
                    "No se encontró el usuario con id {} para suspender",
                    idUsuario
            );

            return new RuntimeException(
                    "Usuario no encontrado"
            );
        } );

        usuario.setEstadoFisico(
                EstadoUsuario.SUSPENDIDO
        );

        usuarioRepository.save(usuario);
        log.info(
        "Usuario {} suspendido correctamente",
        idUsuario
);
    }
    public void activarUsuario(Integer idUsuario) {
log.info(
        "Activando usuario con id {}",
        idUsuario
);
        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->{

            log.warn(
                    "No se encontró el usuario con id {} para activar",
                    idUsuario
            );

            return new RuntimeException(
                    "Usuario no encontrado"
            );
        });

        usuario.setEstadoFisico(
                EstadoUsuario.ACTIVO
        );

        usuarioRepository.save(usuario);
        log.info(
        "Usuario {} activado correctamente",
        idUsuario
);
    } 
}
