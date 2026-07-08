package com.proveperu.m05_gestion_clientes.dto.response;


import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa un producto incluido
 * dentro de una compra realizada por el cliente.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoHistorialCompraResponse {
    
    /**
     * Identificador técnico del producto.
     */
    private Integer idProducto;

    /**
     * Nombre del producto vendido.
     */
    private String nombreProducto;

    /**
     * Cantidad comprada.
     */
    private BigDecimal cantidad;

    /**
     * Subtotal correspondiente al producto.
     */
    private BigDecimal subtotal;
}
