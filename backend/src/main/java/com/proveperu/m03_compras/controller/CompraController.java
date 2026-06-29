package com.proveperu.m03_compras.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proveperu.m03_compras.dto.request.CambiarEstadoCompraRequest;
import com.proveperu.m03_compras.dto.request.RegistrarCompraRequest;
import com.proveperu.m03_compras.dto.request.RegistrarProveedorRequest;
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
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST encargado de exponer
 * los endpoints del módulo de compras.
 */
@Slf4j
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
 log.info("Endpoint GET /api/compras/dashboard llamado");
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
 log.info("Endpoint GET /api/compras/opciones llamado");
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
   log.info("Endpoint GET /api/compras/proveedores llamado");

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
 * Registra un nuevo proveedor para el módulo de compras.
 *
 * @param request datos del proveedor.
 * @return proveedor registrado.
 */
@Operation(
        summary = "Registrar proveedor",
        description = "Registra un nuevo proveedor usando los campos definidos en la base de datos."
)
@PostMapping("/proveedores")
public ResponseEntity<ApiResponse<ProveedorListadoResponse>>
        registrarProveedor(
                @Valid @RequestBody RegistrarProveedorRequest request
        ) {
  log.info("Endpoint POST /api/compras/proveedores llamado para registrar proveedor");
    ProveedorListadoResponse response =
            compraService.registrarProveedor(request);

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Proveedor registrado correctamente"
            )
    );
}
/**
 * Lista las compras realizadas a un proveedor específico.
 *
 * @param idProveedor identificador del proveedor.
 * @return lista de compras del proveedor.
 */
@Operation(
        summary = "Listar compras por proveedor",
        description = "Obtiene las compras registradas para un proveedor específico."
)
@GetMapping("/proveedores/{idProveedor}/compras")
public ResponseEntity<ApiResponse<List<CompraListadoResponse>>>
        listarComprasPorProveedor(
                @PathVariable Integer idProveedor
        ) {
  log.info(
                "Endpoint GET /api/compras/proveedores/{}/compras llamado",
                idProveedor
        );
    List<CompraListadoResponse> response =
            compraService.listarComprasPorProveedor(idProveedor);

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Compras del proveedor obtenidas correctamente"
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
 log.info("Endpoint POST /api/compras llamado para registrar compra");
    if (authentication == null
            || "anonymousUser".equals(authentication.getName())) {

        throw new RuntimeException(
                "Debe iniciar sesión para registrar una compra"
        );
    }
   log.info(
                "Registrando compra desde endpoint. Usuario autenticado: {}",
                authentication.getName()
        );
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

        log.info(
                "Endpoint GET /api/compras llamado. Buscar: {}, Estado: {}",
                buscar,
                estado
        );

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
 * Cambia el estado de una compra.
 *
 * Si el nuevo estado es RECIBIDO, se ejecuta el procedimiento almacenado
 * que registra la recepción y actualiza el stock.
 *
 * @param idCompra identificador de la compra.
 * @param request nuevo estado.
 * @param authentication usuario autenticado.
 * @return detalle actualizado de la compra.
 */
@Operation(
        summary = "Cambiar estado de compra",
        description = "Cambia el estado de una compra. Si cambia a RECIBIDO, registra la recepción y actualiza el stock mediante procedimiento almacenado."
)
@PatchMapping("/{idCompra}/estado")
public ResponseEntity<ApiResponse<CompraDetalleResponse>>
        cambiarEstadoCompra(
                @PathVariable Integer idCompra,
                @Valid @RequestBody CambiarEstadoCompraRequest request,
                Authentication authentication
        ) {
  log.info(
                "Endpoint PATCH /api/compras/{}/estado llamado. Nuevo estado: {}",
                idCompra,
                request.getEstado()
        );
    if (authentication == null
            || "anonymousUser".equals(authentication.getName())) {
        log.warn(
                    "Intento de cambiar estado de compra sin usuario autenticado. IdCompra: {}",
                    idCompra
            );
        throw new RuntimeException("Debe iniciar sesión para cambiar el estado de una compra");
    }

    CompraDetalleResponse response =
            compraService.cambiarEstadoCompra(
                    idCompra,
                    request,
                    authentication.getName()
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    response,
                    "Estado de compra actualizado correctamente"
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
  log.info(
                "Endpoint GET /api/compras/{} llamado para obtener detalle de compra",
                idCompra
        );
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
