package com.proveperu.m06_usuarios.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.proveperu.m06_usuarios.dto.response.PermisoListadoResponse;
import com.proveperu.m06_usuarios.repository.PermisoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
public class PermisoService {
    
    private final PermisoRepository permisoRepository;

    public List<PermisoListadoResponse> listarPermisos() {

       log.info("Listando permisos del sistema");

    return permisoRepository.findAll()
            .stream()
            .map(permiso ->
                    PermisoListadoResponse.builder()
                            .idPermiso(permiso.getId())
                            .modulo(permiso.getModulo().name())
                            .accion(permiso.getAccion().name())
                            .descripcion(permiso.getDescripcion())
                            .estado(permiso.getEstadoFisico().name())
                            .build()
            )
            .collect(Collectors.toList());
    }
}
