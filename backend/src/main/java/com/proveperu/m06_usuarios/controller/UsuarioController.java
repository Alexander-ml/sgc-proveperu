package com.proveperu.m06_usuarios.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proveperu.m06_usuarios.dto.response.UsuarioDashboardResponse;
import com.proveperu.m06_usuarios.dto.response.UsuarioListadoResponse;
import com.proveperu.m06_usuarios.service.UsuarioService;
import com.proveperu.shared.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST encargado de gestionar las operaciones
 * relacionadas con usuarios y roles dentro del sistema.
 *
 * <p>
 * Expone endpoints destinados a la consulta de indicadores,
 * administración de usuarios y gestión de roles.
 * </p>
 *
 * @author David Sanchez
 */
@Tag(
        name = "Usuarios",
        description = "Gestión de usuarios, roles y permisos"
)
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    /**
     * Servicio encargado de ejecutar la lógica de negocio
     * del módulo de usuarios.
     */
    private final UsuarioService usuarioService;

    /**
     * Obtiene los indicadores principales del dashboard
     * del módulo de usuarios.
     *
     * @return información consolidada de usuarios y roles.
     */
    @Operation(
            summary = "Dashboard de usuarios",
            description = "Obtiene los indicadores principales del módulo de usuarios."
    )
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<UsuarioDashboardResponse>> obtenerDashboard() {

        UsuarioDashboardResponse response =
                usuarioService.obtenerDashboard();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Dashboard obtenido correctamente",
                        response
                )
        );
    }
    /**
 * Obtiene la lista de usuarios registrados
 * en el sistema.
 *
 * @param nombre filtro opcional por nombre.
 * @return listado de usuarios.
 */
@Operation(
        summary = "Listar usuarios",
        description = "Obtiene la lista de usuarios registrados en el sistema."
)
@GetMapping
public ResponseEntity<ApiResponse<List<UsuarioListadoResponse>>> listarUsuarios(
        @RequestParam(required = false) String nombre
) {

    List<UsuarioListadoResponse> response =
            usuarioService.listarUsuarios(nombre);

    return ResponseEntity.ok(
            ApiResponse.ok(
                    "Usuarios obtenidos correctamente",
                    response
            )
    );
}
}
