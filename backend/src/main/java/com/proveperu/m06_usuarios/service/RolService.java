package com.proveperu.m06_usuarios.service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proveperu.m06_usuarios.dto.request.ActualizarPermisosRolRequest;
import com.proveperu.m06_usuarios.dto.response.RolDetalleResponse;
import com.proveperu.m06_usuarios.dto.response.RolListadoResponse;
import com.proveperu.m06_usuarios.entity.Permiso;
import com.proveperu.m06_usuarios.entity.Rol;
import com.proveperu.m06_usuarios.entity.RolPermiso;
import com.proveperu.m06_usuarios.entity.RolPermisoId;
import com.proveperu.m06_usuarios.repository.PermisoRepository;
import com.proveperu.m06_usuarios.repository.RolPermisoRepository;
import com.proveperu.m06_usuarios.repository.RolRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
public class RolService {
    
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

private final RolPermisoRepository rolPermisoRepository;

    public List<RolListadoResponse> listarRoles() {

          log.info("Listando roles del sistema");
        List<Rol> roles = rolRepository.findAll();

        return roles.stream()
                .map(rol -> RolListadoResponse.builder()
                        .idRol(rol.getIdRol())
                        .nombreRol(rol.getNombreRol())
                        .descripcion(rol.getDescripcion())
                        .estado(rol.getEstadoFisico().name())
                        .cantidadPermisos(
                                rol.getRolPermisos().size()
                        )
                        .build())
                .collect(Collectors.toList());
    }
    public RolDetalleResponse obtenerRolPorId(Integer idRol) {
   log.info("Consultando rol con id {}", idRol);
    Rol rol = rolRepository.findById(idRol)
            .orElseThrow(() ->
                   {

            log.warn(
                    "No se encontró el rol con id {}",
                    idRol
            );

            return new RuntimeException(
                    "Rol no encontrado"
            );
        } );

    List<String> permisos =
            rol.getRolPermisos()
                    .stream()
                    .map(RolPermiso ->

                            RolPermiso.getPermiso().getModulo().name()
                                    + " - "
                                    + RolPermiso.getPermiso().getAccion().name()

                    )
                    .toList();

    return RolDetalleResponse.builder()
            .idRol(rol.getIdRol())
            .nombreRol(rol.getNombreRol())
            .descripcion(rol.getDescripcion())
            .estado(rol.getEstadoFisico().name())
            .permisos(permisos)
            .build();
}
@Transactional
public void actualizarPermisosRol(
        Integer idRol,
        ActualizarPermisosRolRequest request
) {
  log.info("Actualizando permisos del rol {}", idRol);
    Rol rol = rolRepository.findById(idRol)
            .orElseThrow(() -> {

            log.warn(
                    "Intento de actualizar permisos de un rol inexistente. Id: {}",
                    idRol
            );

            return new RuntimeException(
                    "Rol no encontrado"
            );
        });

    rolPermisoRepository.deleteByRol(rol);

    for (Integer idPermiso : request.getPermisos()) {

        Permiso permiso = permisoRepository.findById(idPermiso)
                .orElseThrow(() -> {

            log.warn(
                    "No se encontró el permiso con id {}",
                    idPermiso
            );

            return new RuntimeException(
                    "Permiso no encontrado"
            );
        });

        RolPermiso rolPermiso = RolPermiso.builder()
                .id_rol_permiso(
                        new RolPermisoId(
                                rol.getIdRol(),
                                permiso.getId()
                        )
                )
                .rol(rol)
                .permiso(permiso)
                .build();
rolPermiso.setFechaHoraCreacion(LocalDateTime.now());
        rolPermisoRepository.save(rolPermiso);
    }
     log.info(
            "Permisos actualizados para el rol {}. Total permisos: {}",
            idRol,
            request.getPermisos().size()
    );
}
}
