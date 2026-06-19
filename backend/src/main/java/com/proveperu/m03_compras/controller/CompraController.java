package com.proveperu.m03_compras.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proveperu.m03_compras.dto.request.RegistrarCompraRequest;
import com.proveperu.m03_compras.dto.response.CompraDashboardResponse;
import com.proveperu.m03_compras.dto.response.CompraDetalleResponse;
import com.proveperu.m03_compras.dto.response.CompraListadoResponse;
import com.proveperu.m03_compras.dto.response.CompraOpcionesResponse;
import com.proveperu.m03_compras.dto.response.ProveedorListadoResponse;
import com.proveperu.m03_compras.service.CompraService;
import com.proveperu.shared.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
 * Obtiene las opciones necesarias para registrar una nueva compra.
 *
 * @return proveedores, métodos de pago y productos disponibles.
 */
@Operation(
        summary = "Opciones para registrar compra",
        description = "Obtiene proveedores, métodos de pago y productos activos para el formulario de nueva compra."
)
@GetMapping("/opciones")
public ResponseEntity<ApiResponse<CompraOpcionesResponse>>
        obtenerOpcionesRegistro() {

    CompraOpcionesResponse response =
            compraService.obtenerOpcionesRegistro();

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Opciones para registrar compra obtenidas correctamente"
            )
    );
}
/**
 * Lista los proveedores registrados para el módulo de compras.
 *
 * @return lista de proveedores.
 */
@Operation(
        summary = "Listar proveedores",
        description = "Obtiene los proveedores registrados para mostrarlos en la pestaña de proveedores del módulo de compras."
)
@GetMapping("/proveedores")
public ResponseEntity<ApiResponse<List<ProveedorListadoResponse>>>
        listarProveedores() {

    List<ProveedorListadoResponse> response =
            compraService.listarProveedores();

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Proveedores obtenidos correctamente"
            )
    );
}
/**
 * Registra una nueva compra a un proveedor.
 *
 * La compra se registra inicialmente como PENDIENTE,
 * sin actualizar stock hasta que se reciba la compra.
 *
 * @param request datos de la compra.
 * @param authentication usuario autenticado que registra la compra.
 * @return detalle de la compra registrada.
 */
@Operation(
        summary = "Registrar compra",
        description = "Registra una nueva compra con proveedor, método de pago y productos. La compra queda inicialmente como PENDIENTE."
)
@PostMapping
public ResponseEntity<ApiResponse<CompraDetalleResponse>>
        registrarCompra(
                @Valid @RequestBody RegistrarCompraRequest request,
                Authentication authentication
        ) {

    if (authentication == null
            || "anonymousUser".equals(authentication.getName())) {

        throw new RuntimeException(
                "Debe iniciar sesión para registrar una compra"
        );
    }

    CompraDetalleResponse response =
            compraService.registrarCompra(
                    request,
                    authentication.getName()
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Compra registrada correctamente"
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
    /**
 * Obtiene el detalle completo de una compra seleccionada.
 *
 * @param idCompra identificador de la compra.
 * @return detalle completo de la compra.
 */
@Operation(
        summary = "Detalle de compra",
        description = "Obtiene el detalle completo de una compra, incluyendo proveedor, productos, método de pago, total y usuario que registró."
)
@GetMapping("/{idCompra}")
public ResponseEntity<ApiResponse<CompraDetalleResponse>>
        obtenerDetalleCompra(
                @PathVariable Integer idCompra
        ) {

    CompraDetalleResponse response =
            compraService.obtenerDetalleCompra(idCompra);

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Detalle de compra obtenido correctamente"
            )
    );
}
}
