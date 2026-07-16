package com.proveperu.m03_compras.enums;

/**
 * Estados posibles de una compra.
 */
public enum EstadoCompra {
    /**
     * Compra registrada pero aún pendiente
     * de recepción completa.
     */
    PENDIENTE,

    /**
     * Recepción parcial de productos.
     */
    PARCIAL,

    /**
     * Compra completamente recibida.
     */
    RECIBIDO,

    /**
     * Compra anulada.
     */
    ANULADO
}
