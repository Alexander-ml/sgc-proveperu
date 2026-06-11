package com.proveperu.m01_ventas.controller;

import com.proveperu.m01_ventas.dto.request.VentaFiltroRequest;
import com.proveperu.m01_ventas.dto.response.VentaResumenResponseDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
@Tag(name = "Ventas", description = "Endpoints para consulta y gestión del módulo de ventas.")
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
                    schema = @Schema(allowableValues = {"ASC", "DES"})
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
}
