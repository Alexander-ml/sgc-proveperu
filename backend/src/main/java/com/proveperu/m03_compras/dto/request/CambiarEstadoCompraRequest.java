package com.proveperu.m03_compras.dto.request;
import com.proveperu.m03_compras.enums.EstadoCompra;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para cambiar el estado de una compra.
 */
@Getter
@Setter
public class CambiarEstadoCompraRequest {
    @NotNull(message = "Debe seleccionar un estado para la compra")
    private EstadoCompra estado;
}
