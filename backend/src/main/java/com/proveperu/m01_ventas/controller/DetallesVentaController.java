package com.proveperu.m01_ventas.controller;

import com.proveperu.m01_ventas.dto.response.VentaDetalleResponseDTO;
import com.proveperu.m01_ventas.service.VentaService;
import com.proveperu.shared.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone el detalle completo de una venta.
 *
 * <p>
 * Recibe el identificador técnico como path variable, lo delega
 * al service y retorna la respuesta estandarizada con toda la
 * información necesaria para la vista de detalle.
 * </p>
 */
@Validated
@RestController
@Tag(name = "Detalle de Ventas", description = "Endpoints para consulta sobre detalles de ventas y gestión del módulo de ventas.")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/detalleventas")
@RequiredArgsConstructor
public class DetallesVentaController {
    private final VentaService ventaService;

    /**
     * Obtiene el detalle completo de una venta.
     *
     * <p>
     * Retorna la cabecera de la venta, el cliente asociado, el vendedor,
     * el comprobante emitido (si existe), los productos vendidos, los pagos
     * aplicados y los valores derivados: subtotalGeneral, montoPagadoTotal y cambio.
     * </p>
     *
     * @param idVenta identificador técnico de la venta (debe ser positivo).
     * @return respuesta estandarizada con el detalle completo de la venta.
     */
    @GetMapping("/{idVenta}")
    // @PreAuthorize("hasAuthority('VENTAS_LEER')")
    @Operation(
            summary     = "Obtener detalle completo de una venta",
            description = """
                    Retorna toda la información de una venta: cabecera, cliente,
                    vendedor, comprobante, productos y pagos, junto con los valores
                    derivados (subtotalGeneral, montoPagadoTotal, cambio).
                    Utilizado para renderizar el modal o panel de detalle al
                    hacer clic sobre una fila del listado de ventas.
                    """
    )
    public ResponseEntity<ApiResponse<VentaDetalleResponseDTO>> obtenerDetalle(
            @Parameter(description = "Identificador técnico de la venta.", required    = true, example     = "2")
            @PathVariable @Positive(message = "El identificador de la venta debe ser un valor positivo.") Integer idVenta) {

        VentaDetalleResponseDTO detalle = ventaService.obtenerDetalleVenta(idVenta);

        return ResponseEntity.ok(
                ApiResponse.success(detalle, "Detalle de venta obtenido exitosamente.")
        );
    }
}
