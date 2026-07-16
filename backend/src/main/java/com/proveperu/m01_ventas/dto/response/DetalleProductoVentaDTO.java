package com.proveperu.m01_ventas.dto.response;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO de una línea de producto dentro del detalle de venta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleProductoVentaDTO {
    /** Identificador técnico del producto. */
    private Integer productoId;

    /** Código único del producto. */
    private String codigoProducto;

    /** Nombre del producto. */
    private String nombreProducto;

    /** Cantidad vendida. */
    private BigDecimal cantidad;

    /** Precio unitario aplicado en la venta. */
    private BigDecimal precioUnitario;

    /** Subtotal calculado para esta línea. */
    private BigDecimal subtotal;
}
