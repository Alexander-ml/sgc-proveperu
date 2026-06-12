package com.proveperu.m06_usuarios.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proveperu.m06_usuarios.dto.request.ActualizarPermisosRolRequest;
import com.proveperu.m06_usuarios.dto.response.RolDetalleResponse;
import com.proveperu.m06_usuarios.dto.response.RolListadoResponse;
import com.proveperu.m06_usuarios.service.RolService;
import com.proveperu.shared.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@Tag(
        name = "Roles",
        description = "Gestión de roles y permisos"
)
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {
      private final RolService rolService;

    @Operation(
            summary = "Listar roles",
            description = "Obtiene la lista de roles registrados en el sistema."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<RolListadoResponse>>> listarRoles() {

        List<RolListadoResponse> response =
                rolService.listarRoles();

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Roles obtenidos correctamente"
                )
        );
    }
    @Operation(
        summary = "Obtener rol por ID",
        description = "Obtiene el detalle de un rol junto con sus permisos."
)
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<RolDetalleResponse>>
obtenerRolPorId(
        @PathVariable Integer id
) {

    RolDetalleResponse response =
            rolService.obtenerRolPorId(id);

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Rol obtenido correctamente"
            )
    );
}
@Operation(
        summary = "Actualizar permisos de un rol",
        description = "Actualiza los permisos asignados a un rol."
)
@PatchMapping("/{id}/permisos")
public ResponseEntity<ApiResponse<Void>>
actualizarPermisosRol(
        @PathVariable Integer id,
        @Valid @RequestBody ActualizarPermisosRolRequest request
) {

    rolService.actualizarPermisosRol(id, request);

    return ResponseEntity.ok(
            ApiResponse.success(
                    null,
                    "Permisos actualizados correctamente"
            )
    );
}
}
