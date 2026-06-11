package com.proveperu.m01_ventas.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO de respuesta completo para la vista de detalle de una venta.
 *
 * <p>
 * Consolida toda la información necesaria para renderizar el panel
 * o modal de detalle: cabecera, cliente, vendedor, comprobante,
 * productos, pagos y valores derivados calculados por el service.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaDetalleResponseDTO {
    /** Identificador técnico de la venta. */
    private Integer idVenta;

    /** Código visual derivado: V-{AÑO}-{ID_6_DÍGITOS}. */
    private String numeroVenta;

    /** Fecha y hora de registro de la venta. */
    private LocalDateTime fechaHoraVenta;

    /** Información del cliente asociado. Puede ser null. */
    private ClienteDetalleVentaDTO cliente;

    /** Información del usuario que registró la venta. */
    private UsuarioDetalleVentaDTO vendedor;

    /** Nombre del rol del usuario vendedor. */
    private String tipoVendedor;

    /** Comprobante emitido. Puede ser null si no se emitió. */
    private ComprobanteDetalleVentaDTO comprobante;

    /** Estado operativo de la venta (REGISTRADA | ANULADA). */
    private String estadoVenta;

    /** Total general almacenado en la venta. */
    private BigDecimal total;

    /** Lista de pagos aplicados a la venta. Nunca null. */
    @Builder.Default
    private List<PagoVentaDetalleDTO> pagos = new ArrayList<>();

    /** Lista de productos incluidos en la venta. Nunca null. */
    @Builder.Default
    private List<DetalleProductoVentaDTO> productos = new ArrayList<>();

    /** Suma calculada de subtotales de todos los productos. */
    private BigDecimal subtotalGeneral;

    /** Suma calculada del monto de todos los pagos activos. */
    private BigDecimal montoPagadoTotal;

    /**
     * Cambio calculado: montoPagadoTotal - total.
     * Cero si montoPagadoTotal es menor o igual al total.
     */
    private BigDecimal cambio;
}
