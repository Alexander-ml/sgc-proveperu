package com.proveperu.m03_compras.dto.request;
import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Request utilizado para registrar un producto
 * dentro de una nueva compra.
 */
@Getter
@Setter
public class RegistrarDetalleCompraRequest {
      /**
     * Identificador del producto seleccionado.
     */
    @NotNull(message = "Debe seleccionar un producto")
    private Integer idProducto;

    /**
     * Cantidad comprada del producto.
     */
    @NotNull(message = "Debe ingresar la cantidad")
    @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a 0")
    private BigDecimal cantidad;

    /**
     * Precio unitario de compra.
     */
    @NotNull(message = "Debe ingresar el precio unitario de compra")
    @DecimalMin(value = "0.00", message = "El precio unitario no puede ser negativo")
    private BigDecimal precioUnitarioCompra;
}
