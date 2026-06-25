package com.proveperu.m01_ventas.controller;

import com.proveperu.m01_ventas.dto.request.VentaCreateRequest;
import com.proveperu.m01_ventas.dto.request.VentaFiltroRequest;
import com.proveperu.m01_ventas.dto.response.*;
import com.proveperu.m01_ventas.service.VentaService;

import com.proveperu.m01_ventas.validators.VentaFiltroValidator;
import com.proveperu.shared.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST encargado de exponer el listado paginado de ventas
 * con filtros opcionales para consulta y análisis operativo.
 *
 * <p>
 * Este componente recibe los parámetros desde la capa web, construye el
 * filtro funcional correspondiente, ejecuta su validación y delega la
 * consulta al servicio de aplicación.
 * </p>
 */
@RestController
@Tag(name = "Ventas", description = "Operaciones del módulo de ventas: listados de apoyo y registro de ventas")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {
    private final VentaService ventaService;

    /**
     * Obtiene el listado paginado de ventas aplicando filtros opcionales
     * sobre cliente, comprobante, estado, método de pago y rango de fechas.
     *
     * @param q búsqueda global sobre datos visibles de la venta.
     * @param clienteId identificador del cliente.
     * @param numeroVenta número visual de venta.
     * @param tipoComprobante tipo documental asociado.
     * @param estadoVenta estado funcional de la venta.
     * @param metodoPagoId identificador del método de pago.
     * @param fechaInicio fecha y hora inicial del rango.
     * @param fechaFin fecha y hora final del rango.
     * @param page número de página solicitada.
     * @param size tamaño de página solicitado.
     * @param sort campo utilizado para ordenar.
     * @param direction dirección del ordenamiento.
     * @return respuesta estandarizada con el paginado de ventas.
     */
    @GetMapping
    // @PreAuthorize("hasAuthority('VENTAS_LEER')")
    @Operation(
            summary = "Listar ventas paginadas",
            description = """
                    Retorna un listado paginado de ventas con filtros opcionales.
                    Permite búsquedas por cliente, comprobante, estado, método de pago,
                    número visual de venta y rango de fechas.
                    El ordenamiento se aplica sobre campos permitidos por el contrato
                    funcional del servicio.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Ventas obtenidas exitosamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Parámetros inválidos o filtro de negocio incorrecto.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "No autenticado.",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "No autorizado para consultar ventas.",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno no controlado.",
                    content = @Content(mediaType = "application/json")
            )
    })
    @Parameters({
            @Parameter(
                    name = "q",
                    description = "Búsqueda global sobre cliente, serie o correlativo del comprobante, o número visual de venta.",
                    example = "panadería"
            ),
            @Parameter(
                    name = "clienteId",
                    description = "Identificador exacto del cliente.",
                    example = "15"
            ),
            @Parameter(
                    name = "numeroVenta",
                    description = "Número visual de la venta en formato funcional, por ejemplo: V-2026-000042.",
                    example = "V-2026-000042"
            ),
            @Parameter(
                    name = "tipoComprobante",
                    description = "Tipo de comprobante permitido por el sistema.",
                    example = "BOLETA",
                    schema = @Schema(allowableValues = {"BOLETA", "FACTURA", "NOTA"})
            ),
            @Parameter(
                    name = "estadoVenta",
                    description = "Estado funcional de la venta.",
                    example = "REGISTRADA",
                    schema = @Schema(allowableValues = {"REGISTRADA", "ANULADA"})
            ),
            @Parameter(
                    name = "metodoPagoId",
                    description = "Identificador del método de pago asociado a la venta.",
                    example = "3"
            ),
            @Parameter(
                    name = "fechaInicio",
                    description = "Fecha y hora inicial del rango de búsqueda en formato ISO-8601.",
                    example = "2026-06-01T00:00:00"
            ),
            @Parameter(
                    name = "fechaFin",
                    description = "Fecha y hora final del rango de búsqueda en formato ISO-8601.",
                    example = "2026-06-30T23:59:59"
            ),
            @Parameter(
                    name = "page",
                    description = "Número de página solicitado, iniciando en 0.",
                    example = "0"
            ),
            @Parameter(
                    name = "size",
                    description = "Cantidad de registros por página.",
                    example = "20"
            ),
            @Parameter(
                    name = "sort",
                    description = "Campo de ordenamiento permitido por el contrato funcional.",
                    example = "fechaHoraVenta",
                    schema = @Schema(allowableValues = {"idVenta", "fechaHoraVenta", "total"})
            ),
            @Parameter(
                    name = "direction",
                    description = "Dirección del ordenamiento.",
                    example = "DESC",
                    schema = @Schema(allowableValues = {"ASC", "DESC"})
            )
    })
    public ResponseEntity<ApiResponse<Page<VentaResumenResponseDTO>>> listarVentas(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) String numeroVenta,
            @RequestParam(required = false) String tipoComprobante,
            @RequestParam(required = false) String estadoVenta,
            @RequestParam(required = false) Integer metodoPagoId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "fechaHoraVenta") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        VentaFiltroRequest filtro = new VentaFiltroRequest();
        filtro.setQ(q);
        filtro.setClienteId(clienteId);
        filtro.setNumeroVenta(numeroVenta);
        filtro.setTipoComprobante(tipoComprobante);
        filtro.setEstadoVenta(estadoVenta);
        filtro.setMetodoPagoId(metodoPagoId);
        filtro.setFechaInicio(fechaInicio);
        filtro.setFechaFin(fechaFin);
        filtro.setPage(page);
        filtro.setSize(size);
        filtro.setSort(sort);
        filtro.setDirection(direction);

        Page<VentaResumenResponseDTO> resultado = ventaService.listarVentas(filtro);

        return ResponseEntity.ok(
                ApiResponse.success(resultado, "Ventas obtenidas exitosamente."));
    }


    // GET /ventas/clientes — Endpoint 1
    // =========================================================================

    /**
     * Retorna el listado de clientes activos disponibles para asignar a una venta.
     *
     * <p>
     * El frontend puede agregar la opción "Sin cliente" de forma independiente.
     * Si no hay clientes activos la respuesta retorna lista vacía con HTTP 200.
     * </p>
     *
     * @return listado de clientes activos con datos básicos de identificación.
     */
    @Operation(
            summary     = "Listar clientes activos para venta",
            description = "Retorna todos los clientes con estado ACTIVO. Incluye ID, tipo, nombre y RUC."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description  = "Lista de clientes recuperada exitosamente",
                    content = @Content(schema = @Schema(implementation = ClienteListadoResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description  = "No autenticado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description  = "Sin permisos para acceder al módulo de ventas"
            )
    })
    @GetMapping("/clientes")
    public ResponseEntity<ApiResponse<List<ClienteListadoResponse>>> listarClientes() {
        List<ClienteListadoResponse> clientes = ventaService.obtenerClientesParaVenta();
        return ResponseEntity.ok(
                ApiResponse.success(clientes, "Clientes recuperados exitosamente")
        );
    }

    // =========================================================================
    // GET /ventas/tipo-comprobantes — Endpoint 2
    // =========================================================================

    /**
     * Retorna los tipos de comprobante disponibles para emitir en una venta.
     *
     * <p>
     * Los tipos se generan desde el enum {@code TipoComprobante}
     * sin consultas a base de datos.
     * </p>
     *
     * @return listado de tipos de comprobante con código y descripción.
     */
    @Operation(
            summary     = "Listar tipos de comprobante",
            description = "Retorna los tipos de comprobante disponibles: BOLETA, FACTURA, NOTA."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description  = "Tipos de comprobante recuperados exitosamente",
                    content = @Content(schema = @Schema(implementation = TipoComprobanteResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description  = "No autenticado"
            )
    })
    @GetMapping("/tipo-comprobantes")
    public ResponseEntity<ApiResponse<List<TipoComprobanteResponse>>> listarTiposComprobante() {
        List<TipoComprobanteResponse> tipos = ventaService.obtenerTiposComprobante();
        return ResponseEntity.ok(
                ApiResponse.success(tipos, "Tipos de comprobante recuperados exitosamente")
        );
    }

    // =========================================================================
    // GET /ventas/metodos-pago — Endpoint 3
    // =========================================================================

    /**
     * Retorna los métodos de pago habilitados para registrar en una venta.
     *
     * <p>
     * Solo se retornan métodos de pago con estado activo.
     * Si no hay métodos disponibles retorna lista vacía.
     * </p>
     *
     * @return listado de métodos de pago activos con ID y nombre.
     */
    @Operation(
            summary     = "Listar métodos de pago activos",
            description = "Retorna todos los métodos de pago con estado ACTIVO (efectivo, transferencia, YAPE, POS, etc.)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description  = "Métodos de pago recuperados exitosamente",
                    content = @Content(schema = @Schema(implementation = MetodoPagoResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description  = "No autenticado"
            )
    })
    @GetMapping("/metodos-pago")
    public ResponseEntity<ApiResponse<List<MetodoPagoResponse>>> listarMetodosPago() {
        List<MetodoPagoResponse> metodos = ventaService.obtenerMetodosPago();
        return ResponseEntity.ok(
                ApiResponse.success(metodos, "Métodos de pago recuperados exitosamente")
        );
    }

    // =========================================================================
    // GET /ventas/productos — Endpoint 4
    // =========================================================================

    /**
     * Busca productos activos por nombre para agregarlos a una nueva venta.
     *
     * <p>
     * La búsqueda es parcial e insensible a mayúsculas/minúsculas.
     * Cada resultado incluye el stock actual disponible.
     * Retorna lista vacía si no hay coincidencias.
     * </p>
     *
     * @param nombre texto parcial a buscar en el nombre del producto (requerido).
     * @return listado de productos con stock y precio unitario.
     */
    @Operation(
            summary     = "Buscar productos para venta",
            description = "Busca productos activos cuyo nombre contenga el texto indicado. Incluye stock actual y precio unitario."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description  = "Productos recuperados exitosamente",
                    content = @Content(schema = @Schema(implementation = ProductoVentaResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description  = "El parámetro 'nombre' es obligatorio y no puede estar vacío"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description  = "No autenticado"
            )
    })
    @GetMapping("/productos")
    public ResponseEntity<ApiResponse<List<ProductoVentaResponse>>> buscarProductos(
            @Parameter(description = "Texto parcial del nombre del producto", required = true)
            @RequestParam
            @NotBlank(message = "El parámetro 'nombre' es obligatorio y no puede estar vacío")
            String nombre
    ) {
        List<ProductoVentaResponse> productos = ventaService.buscarProductosParaVenta(nombre);
        return ResponseEntity.ok(
                ApiResponse.success(productos, "Productos recuperados exitosamente")
        );
    }

    // =========================================================================
    // POST /ventas — Endpoint 5
    // =========================================================================

    /**
     * Registra una nueva venta completa de forma transaccional.
     *
     * <p>
     * Procesa la cabecera, detalle de productos y pago. Valida stock,
     * calcula totales, actualiza inventario, registra movimiento de caja
     * y genera el comprobante correspondiente (RF-01 a RF-25).
     * </p>
     *
     * <p>
     * El usuario responsable de la venta se extrae automáticamente
     * del token JWT autenticado.
     * </p>
     *
     * @param request       datos completos de la venta a registrar.
     * @param userDetails   información del usuario autenticado (inyectada por Spring Security).
     * @return datos de confirmación: ID de venta, cambio a devolver y comprobante.
     */
    @Operation(
            summary     = "Crear nueva venta",
            description = "Registra una venta completa con detalle de productos y pago. " +
                    "Actualiza inventario y registra movimiento de caja de forma automática."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description  = "Venta registrada exitosamente",
                    content = @Content(schema = @Schema(implementation = VentaCreateResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description  = "Datos de entrada inválidos (Bean Validation)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description  = "Cliente, producto o método de pago no encontrado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "422",
                    description  = "Regla de negocio violada (stock insuficiente, monto insuficiente, caja cerrada)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description  = "No autenticado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description  = "Sin permisos para crear ventas"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<VentaCreateResponse>> crearVenta(
            @Valid @RequestBody VentaCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        VentaCreateResponse response = ventaService.crearVenta(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Venta registrada exitosamente"));
    }
}
