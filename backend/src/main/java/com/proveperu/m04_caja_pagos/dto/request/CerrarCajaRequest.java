package com.proveperu.m04_caja_pagos.dto.request;
import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para cerrar una caja operativa.
 */
@Getter
@Setter
public class CerrarCajaRequest {
    
    /**
     * Saldo físico contado por el responsable de caja.
     *
     * Este valor corresponde a la columna saldo_real
     * de la tabla cierre_caja.
     */
    @NotNull(message = "El saldo real contado es obligatorio")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "El saldo real no puede ser negativo"
    )
    @Digits(
            integer = 10,
            fraction = 2,
            message = "El saldo real debe tener como máximo 10 enteros y 2 decimales"
    )
    private BigDecimal saldoReal;
}
