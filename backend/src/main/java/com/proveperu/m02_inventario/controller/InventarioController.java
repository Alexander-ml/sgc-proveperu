package com.proveperu.m02_inventario.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proveperu.m02_inventario.dto.request.RegistrarProductoRequest;
import com.proveperu.m02_inventario.dto.response.InventarioDashboardResponse;
import com.proveperu.m02_inventario.dto.response.ProductoInventarioResponse;
import com.proveperu.m02_inventario.service.InventarioService;
import com.proveperu.shared.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Inventario",
        description = "Gestión de productos, stock y alertas de inventario"
)
@SecurityRequirement(name = "bearerAuth")
public class InventarioController {
    
    private final InventarioService inventarioService;

    @Operation(
            summary = "Obtener dashboard de inventario",
            description = "Obtiene los indicadores principales: total de productos, "
                    + "productos sin stock, productos con stock bajo y productos disponibles."
    )
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<InventarioDashboardResponse>>
            obtenerDashboard() {

        log.info("Endpoint GET /api/inventario/dashboard llamado");

        InventarioDashboardResponse response =
                inventarioService.obtenerDashboard();

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Dashboard de inventario obtenido correctamente"
                )
        );
    }

    @Operation(
            summary = "Listar productos de inventario",
            description = "Lista los productos activos con su stock actual. "
                    + "Permite buscar por código o nombre y filtrar por estado de stock."
    )
    @GetMapping("/productos")
    public ResponseEntity<ApiResponse<List<ProductoInventarioResponse>>>
            listarProductosInventario(
                    @RequestParam(required = false) String buscar,
                    @RequestParam(required = false) String estadoStock
            ) {

        log.info(
                "Endpoint GET /api/inventario/productos llamado. Buscar: {}, EstadoStock: {}",
                buscar,
                estadoStock
        );

        List<ProductoInventarioResponse> response =
                inventarioService.listarProductosInventario(
                        buscar,
                        estadoStock
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Productos de inventario obtenidos correctamente"
                )
        );
    }

    @Operation(
            summary = "Registrar producto",
            description = "Registra un nuevo producto en el catálogo y crea su stock inicial."
    )
    @PostMapping("/productos")
    public ResponseEntity<ApiResponse<ProductoInventarioResponse>>
            registrarProducto(
                    @Valid @RequestBody RegistrarProductoRequest request
            ) {

        log.info(
                "Endpoint POST /api/inventario/productos llamado. Codigo: {}, Nombre: {}",
                request.getCodigoProducto(),
                request.getNombreProducto()
        );

        ProductoInventarioResponse response =
                inventarioService.registrarProducto(
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Producto registrado correctamente"
                )
        );
    }
}
