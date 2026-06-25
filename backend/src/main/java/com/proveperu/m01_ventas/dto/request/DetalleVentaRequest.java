package com.proveperu.m01_ventas.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO de solicitud que representa una línea de detalle
 * dentro de una venta (un producto con su cantidad y precio).
 *
 * <p>
 * Es usado como elemento de la lista {@code productos}
 * en {@link VentaCreateRequest}.
 * </p>
 *
 * <p>
 * Las validaciones de stock se realizan en el servicio;
 * aquí solo se valida el formato y los rangos básicos.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
public class DetalleVentaRequest {

    /**
     * Identificador del producto a vender.
     * Debe existir en el catálogo de productos activos.
     */
    @NotNull(message = "El identificador del producto es obligatorio")
    private Integer idProducto;

    /**
     * Cantidad a vender del producto.
     * Debe ser mayor a cero.
     */
    @NotNull(message = "La cantidad es obligatoria")
    @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a cero")
    private BigDecimal cantidad;

    /**
     * Precio unitario a aplicar en esta venta.
     * Puede diferir del precio estándar (RF-05).
     * Debe ser mayor o igual a cero.
     */
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.00", message = "El precio unitario no puede ser negativo")
    private BigDecimal precioUnitario;
}