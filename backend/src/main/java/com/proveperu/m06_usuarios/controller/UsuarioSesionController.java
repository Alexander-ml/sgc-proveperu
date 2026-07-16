package com.proveperu.m06_usuarios.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proveperu.m06_usuarios.dto.response.HistorialAccesoResponse;
import com.proveperu.m06_usuarios.service.UsuarioSesionService;
import com.proveperu.shared.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Tag(
        name = "Historial de accesos",
        description = "Consulta de los accesos realizados por los usuarios"
)
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UsuarioSesionController {
      private final UsuarioSesionService usuarioSesionService;

    /**
     * Lista los inicios de sesión registrados.
     */
    @Operation(
            summary = "Listar historial de accesos",
            description = "Obtiene los inicios de sesión registrados, ordenados desde el más reciente."
    )
    @GetMapping("/historial-accesos")
    public ResponseEntity<ApiResponse<List<HistorialAccesoResponse>>>
            listarHistorialAccesos() {
log.info("Endpoint GET /api/usuarios/historial-accesos llamado");

        List<HistorialAccesoResponse> historial =
                usuarioSesionService.listarHistorialAccesos();

        return ResponseEntity.ok(
                ApiResponse.success(
                        historial,
                        "Historial de accesos obtenido correctamente"
                )
        );
    }
}
