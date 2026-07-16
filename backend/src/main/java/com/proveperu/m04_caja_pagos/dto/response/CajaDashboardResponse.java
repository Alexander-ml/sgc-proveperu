package com.proveperu.m04_caja_pagos.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para mostrar los indicadores principales
 * del módulo de caja y pagos.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CajaDashboardResponse {
    
    /**
     * Identificador de la caja operativa.
     */
    private Integer idCaja;

    /**
     * Nombre descriptivo de la caja.
     */
    private String nombreCaja;

    /**
     * Estado actual de la caja.
     *
     * Valores posibles: ABIERTA, CERRADA o INACTIVA.
     */
    private String estadoCaja;

    /**
     * Identificador de la apertura activa.
     */
    private Integer idAperturaCaja;

    /**
     * Monto inicial registrado al abrir caja.
     */
    private BigDecimal montoApertura;

    /**
     * Saldo actual calculado de caja.
     */
    private BigDecimal saldoActual;

    /**
     * Total de ingresos registrados durante la apertura activa.
     */
    private BigDecimal totalIngresos;

    /**
     * Total de egresos registrados durante la apertura activa.
     */
    private BigDecimal totalEgresos;

    /**
     * Cantidad total de movimientos del día o apertura activa.
     */
    private Integer cantidadMovimientos;

    /**
     * Usuario que realizó la apertura de caja.
     */
    private String abiertaPor;

    /**
     * Fecha y hora de apertura.
     */
    private LocalDateTime fechaHoraApertura;
}
