package com.proveperu.m01_ventas.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * DTO de respuesta que representa un producto disponible
 * para ser agregado a una venta.
 *
 * <p>
 * Incluye el stock actual disponible en el módulo de
 * inventario y el precio unitario por defecto del producto,
 * que puede ser modificado por el usuario al crear la venta
 * (RF-05).
 * </p>
 *
 * <p>
 * Se utiliza en el endpoint {@code GET /ventas/productos}.
 * </p>
 */
@Getter
@Builder
public class ProductoVentaResponse {

    /**
     * Identificador único del producto.
     */
    private Integer idProducto;

    /**
     * Código único del producto sin espacios.
     */
    private String codigoProducto;

    /**
     * Nombre descriptivo del producto.
     */
    private String nombreProducto;

    /**
     * Unidad de medida del producto.
     * Ejemplo: "UNIDAD", "KG", "LITRO".
     */
    private String unidadMedida;

    /**
     * Cantidad disponible actualmente en stock.
     * Proviene de la tabla {@code stock}.
     */
    private BigDecimal stockActual;

    /**
     * Stock mínimo configurado para alertas de reposición.
     */
    private BigDecimal stockMinimo;
}