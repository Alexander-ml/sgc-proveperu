package com.proveperu.m03_compras.dto.request;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Request utilizado para registrar una nueva compra
 * a un proveedor.
 */
@Getter
@Setter
public class RegistrarCompraRequest {
     /**
     * Proveedor seleccionado para la compra.
     */
    @NotNull(message = "Debe seleccionar un proveedor")
    private Integer idProveedor;

    /**
     * Método de pago utilizado para la compra.
     */
    @NotNull(message = "Debe seleccionar un método de pago")
    private Integer idMetodoPago;

    /**
     * Productos incluidos en la compra.
     */
    @Valid
    @NotEmpty(message = "Debe agregar al menos un producto a la compra")
    private List<RegistrarDetalleCompraRequest> productos;
}
