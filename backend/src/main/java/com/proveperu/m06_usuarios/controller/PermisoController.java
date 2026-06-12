package com.proveperu.m06_usuarios.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proveperu.m06_usuarios.dto.response.PermisoListadoResponse;
import com.proveperu.m06_usuarios.service.PermisoService;
import com.proveperu.shared.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(
        name = "Permisos",
        description = "Gestión de permisos del sistema"
)
@RestController
@RequestMapping("/api/permisos")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class PermisoController {
       private final PermisoService permisoService;

    @Operation(
            summary = "Listar permisos",
            description = "Obtiene todos los permisos registrados en el sistema."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<PermisoListadoResponse>>> listarPermisos() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        permisoService.listarPermisos(),
                        "Permisos obtenidos correctamente"
                )
        );
    }
}
