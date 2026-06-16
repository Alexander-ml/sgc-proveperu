package com.proveperu.m06_usuarios.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proveperu.m06_usuarios.dto.response.HistorialAccesoResponse;
import com.proveperu.m06_usuarios.entity.UsuarioSesion;
import com.proveperu.m06_usuarios.repository.UsuarioSesionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioSesionService {
      
    private final UsuarioSesionRepository usuarioSesionRepository;

    /**
     * Obtiene el historial de inicios de sesión,
     * ordenado desde el acceso más reciente.
     *
     * @return lista de accesos registrados.
     */
   @Transactional(readOnly = true)
public List<HistorialAccesoResponse> listarHistorialAccesos() {

    log.info("Consultando historial de accesos de usuarios");

    List<UsuarioSesion> sesiones =
            usuarioSesionRepository.findAllByOrderByFechaHoraInicioDesc();

    List<HistorialAccesoResponse> historial = new ArrayList<>();

    for (UsuarioSesion sesion : sesiones) {

        // Registro de inicio de sesión
        historial.add(
                HistorialAccesoResponse.builder()
                        .idSesion(sesion.getIdUsuarioSesion())
                        .fechaHora(sesion.getFechaHoraInicio())
                        .usuario(
                                sesion.getUsuario().getNombreCompleto()
                        )
                        .usuarioLogin(
                                sesion.getUsuario().getUsuarioLogin()
                        )
                        .accionRegistrada("Inicio de sesión")
                        .build()
        );

        // Registro de cierre, solamente si ya cerró sesión
        if (sesion.getFechaHoraFin() != null) {

            historial.add(
                    HistorialAccesoResponse.builder()
                            .idSesion(sesion.getIdUsuarioSesion())
                            .fechaHora(sesion.getFechaHoraFin())
                            .usuario(
                                    sesion.getUsuario().getNombreCompleto()
                            )
                            .usuarioLogin(
                                    sesion.getUsuario().getUsuarioLogin()
                            )
                            .accionRegistrada("Cierre de sesión")
                            .build()
            );
        }
    }

    // Ordenar todos los eventos desde el más reciente
    historial.sort(
            Comparator.comparing(
                    HistorialAccesoResponse::getFechaHora
            ).reversed()
    );

    log.info(
            "Historial de accesos consultado. Total de registros: {}",
            historial.size()
    );

    return historial;
}
}
