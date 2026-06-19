package com.proveperu.m03_compras.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proveperu.m03_compras.dto.response.CompraDashboardResponse;
import com.proveperu.m03_compras.dto.response.CompraListadoResponse;
import com.proveperu.m03_compras.service.CompraService;
import com.proveperu.shared.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST encargado de exponer
 * los endpoints del módulo de compras.
 */
@Tag(
        name = "Compras",
        description = "Gestión y consulta de compras a proveedores"
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {
     private final CompraService compraService;

    /**
     * Obtiene los indicadores principales del módulo de compras.
     *
     * @return dashboard de compras.
     */
    @Operation(
            summary = "Dashboard de compras",
            description = "Obtiene el total de compras, monto invertido, compras recibidas y compras pendientes."
    )
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<CompraDashboardResponse>>
            obtenerDashboard() {

        CompraDashboardResponse response =
                compraService.obtenerDashboard();

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Dashboard de compras obtenido correctamente"
                )
        );
    }

    /**
     * Lista las compras registradas en el sistema.
     *
     * @param buscar filtro opcional por proveedor o número de compra.
     * @param estado filtro opcional por estado.
     * @return listado de compras.
     */
    @Operation(
            summary = "Listar compras",
            description = "Lista las compras registradas, permitiendo buscar por proveedor o número de compra y filtrar por estado."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<CompraListadoResponse>>>
            listarCompras(
                    @RequestParam(required = false) String buscar,
                    @RequestParam(required = false) String estado
            ) {

        List<CompraListadoResponse> response =
                compraService.listarCompras(buscar, estado);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Compras obtenidas correctamente"
                )
        );
    }
}
