package com.proveperu.m01_ventas.dto.response;

import lombok.*;

import java.math.BigDecimal;

/**
 * Representa un método de pago aplicado a una venta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoPagoVentaResumenDTO {
    /** Identificador del metodo de pago. */
    private Integer idMetodoPago;

    /** Nombre del metodo pago. */
    private String nombreMetodoPago;

    /** Total Pagado */
    private BigDecimal monto;
}
