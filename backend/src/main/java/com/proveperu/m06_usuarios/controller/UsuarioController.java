package com.proveperu.m06_usuarios.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proveperu.m06_usuarios.dto.request.CambiarPasswordRequest;
import com.proveperu.m06_usuarios.dto.request.CrearUsuarioRequest;
import com.proveperu.m06_usuarios.dto.request.EditarUsuarioRequest;
import com.proveperu.m06_usuarios.dto.response.UsuarioDashboardResponse;
import com.proveperu.m06_usuarios.dto.response.UsuarioDetalleResponse;
import com.proveperu.m06_usuarios.dto.response.UsuarioListadoResponse;
import com.proveperu.m06_usuarios.service.UsuarioService;
import com.proveperu.shared.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
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
  log.info("Endpoint GET /api/usuarios/dashboard llamado");
        UsuarioDashboardResponse response =
                usuarioService.obtenerDashboard();

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Dashboard obtenido correctamente"
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
  log.info(
                "Endpoint GET /api/usuarios llamado. Filtro nombre: {}",
                nombre
        );

        List<UsuarioListadoResponse> response =
                usuarioService.listarUsuarios(nombre);

        return ResponseEntity.ok(
                ApiResponse.success(
                    response,
                        "Usuarios obtenidos correctamente"
                )
        );
    }


    @Operation(
            summary = "Crear usuario",
            description = "Registra un nuevo usuario en el sistema."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<String>> crearUsuario(
            @Valid @RequestBody CrearUsuarioRequest request
    ) {
    log.info("Endpoint POST /api/usuarios llamado para crear usuario");
        usuarioService.crearUsuario(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                    null,
                    "Usuario creado correctamente"

                )
        );
    }


    @Operation(
            summary = "Editar usuario",
            description = "Actualiza la información de un usuario existente."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> editarUsuario(
            @PathVariable Integer id,
            @Valid @RequestBody EditarUsuarioRequest request
    ) {
  log.info(
                "Endpoint PUT /api/usuarios/{} llamado para editar usuario",
                id
        );
        usuarioService.editarUsuario(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                    null,
                    "Usuario actualizado correctamente"
                )
        );
    }


    @Operation(
            summary = "Obtener usuario por ID",
            description = "Obtiene la información completa de un usuario."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioDetalleResponse>>
    obtenerUsuarioPorId(
            @PathVariable Integer id
    ) {
  log.info(
                "Endpoint GET /api/usuarios/{} llamado para obtener detalle de usuario",
                id
        );
        UsuarioDetalleResponse response =
                usuarioService.obtenerUsuarioPorId(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                    response,
                    "Usuario obtenido correctamente"

                )
        );
    }


    @Operation(
            summary = "Suspender usuario",
            description = "Cambia el estado del usuario a SUSPENDIDO."
    )
    @PatchMapping("/{id}/suspender")
    public ResponseEntity<ApiResponse<Void>>
    suspenderUsuario(
            @PathVariable Integer id
    ) {
   log.info(
                "Endpoint PATCH /api/usuarios/{}/suspender llamado",
                id
        );
        usuarioService.suspenderUsuario(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                    null,
                        "Usuario suspendido correctamente"
                )
        );
    }


    @Operation(
            summary = "Activar usuario",
            description = "Cambia el estado del usuario a ACTIVO."
    )
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>>
    activarUsuario(
            @PathVariable Integer id
    ) {
  log.info(
                "Endpoint PATCH /api/usuarios/{}/activar llamado",
                id
        );
        usuarioService.activarUsuario(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                    null,
                    "Usuario activado correctamente"
                )
        );
    }
/**
 * Cambia la contraseña de un usuario existente.
 *
 * <p>
 * Este endpoint permite actualizar la contraseña de un usuario
 * identificado por su ID. La nueva contraseña se recibe mediante
 * un DTO, se valida y se envía al servicio para ser encriptada
 * antes de almacenarse en la base de datos.
 * </p>
 *
 * @param id identificador del usuario.
 * @param request datos que contienen la nueva contraseña.
 * @return respuesta indicando que la contraseña fue actualizada correctamente.
 */
    @Operation(
        summary = "Cambiar contraseña de usuario",
        description = "Actualiza la contraseña de un usuario existente."
)
@PatchMapping("/{id}/password")
public ResponseEntity<ApiResponse<Void>>
cambiarPassword(
        @PathVariable Integer id,
        @Valid @RequestBody CambiarPasswordRequest request
) {
  log.info(
                "Endpoint PATCH /api/usuarios/{}/password llamado para cambiar contraseña",
                id
        );
    usuarioService.cambiarPassword(id, request);

    return ResponseEntity.ok(
            ApiResponse.success(
                    null,
                    "Contraseña actualizada correctamente"
            )
    );
}
}
