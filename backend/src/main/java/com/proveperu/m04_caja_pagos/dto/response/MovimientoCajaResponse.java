package com.proveperu.m04_caja_pagos.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para mostrar un movimiento dentro
 * del módulo de caja y pagos.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCajaResponse {
     /**
     * Identificador técnico del movimiento.
     */
    private Integer idMovimientoCaja;

    /**
     * Fecha y hora en que se registró el movimiento.
     */
    private LocalDateTime fechaHoraMovimiento;

    /**
     * Tipo del movimiento.
     *
     * Valores posibles: INGRESO o EGRESO.
     */
    private String tipoMovimiento;

    /**
     * Concepto o descripción del movimiento.
     */
    private String descripcion;

    /**
     * Método de pago utilizado.
     *
     * Ejemplos: EFECTIVO, TARJETA, TRANSFERENCIA, YAPE, PLIN.
     */
    private String metodoPago;

    /**
     * Monto del movimiento.
     */
    private BigDecimal monto;

    /**
     * Estado físico del movimiento.
     *
     * Valores posibles: REGISTRADO o ANULADO.
     */
    private String estadoMovimiento;

    /**
     * Nombre del usuario que registró el movimiento.
     */
    private String registradoPor;

    /**
     * Identificador de la venta asociada, si corresponde.
     */
    private Integer idVenta;

    /**
     * Identificador de la compra asociada, si corresponde.
     */
    private Integer idCompra; 
}
