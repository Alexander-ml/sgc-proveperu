package com.proveperu.m03_compras.dto.response;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Respuesta utilizada para mostrar un producto
 * dentro del detalle de una compra.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCompraResponse {
    
    private Integer idProducto;

    private String producto;

    private BigDecimal cantidad;

    private BigDecimal precioCompra;

    private BigDecimal subtotal;
}
