package com.proveperu.m01_ventas.dto.response;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO de un pago individual aplicado a la venta en el contexto del detalle.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoVentaDetalleDTO {
    /** Identificador técnico del pago. */
    private Integer idPago;

    /** Identificador del método de pago utilizado. */
    private Integer metodoPagoId;

    /** Nombre del método de pago. */
    private String metodoPagoNombre;

    /** Monto pagado con este método. */
    private BigDecimal monto;
}
