package com.proveperu.m03_compras.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Respuesta utilizada para mostrar una compra
 * dentro de la tabla principal del módulo.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraListadoResponse {
     /**
     * Identificador interno de la compra.
     */
    private Integer idCompra;

    /**
     * Número visual de compra.
     * Ejemplo: C-2026-0001.
     */
    private String numeroCompra;

    /**
     * Fecha y hora de registro de la compra.
     */
    private LocalDateTime fecha;

    /**
     * Razón social del proveedor.
     */
    private String proveedor;

    /**
     * Cantidad de productos asociados a la compra.
     */
    private String productos;

    /**
     * Total de la compra.
     */
    private BigDecimal total;

    /**
     * Método de pago utilizado.
     */
    private String metodoPago;

    /**
     * Estado físico de la compra.
     * Ejemplo: PENDIENTE, RECIBIDO, PARCIAL, ANULADO.
     */
    private String estado;

    /**
     * Usuario que registró la compra.
     */
    private String registradoPor;
}
