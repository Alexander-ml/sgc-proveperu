package com.proveperu.m04_caja_pagos.dto.request;
import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para registrar un egreso manual
 * dentro de una caja operativa.
 */
@Getter
@Setter
public class RegistrarEgresoCajaRequest {
   
    /**
     * Método de pago utilizado para el egreso.
     *
     * Corresponde a la columna id_metodo_pago
     * de la tabla movimiento_caja.
     */
    @NotNull(message = "Debe seleccionar un método de pago")
    @Positive(message = "El método de pago debe ser válido")
    private Integer idMetodoPago;

    /**
     * Monto del egreso registrado.
     *
     * Corresponde a la columna monto
     * de la tabla movimiento_caja.
     */
    @NotNull(message = "El monto del egreso es obligatorio")
    @DecimalMin(
            value = "0.01",
            inclusive = true,
            message = "El monto del egreso debe ser mayor a cero"
    )
    @Digits(
            integer = 10,
            fraction = 2,
            message = "El monto debe tener como máximo 10 enteros y 2 decimales"
    )
    private BigDecimal monto;

    /**
     * Des debe tener como máximo 10 enteros y 2 decimales"
    )
    private BigDecimal monto;

    /**
     * Descripción o motivo del egreso.
     *
     * Corresponde a la columna descripcion
     * de la tabla movimiento_caja.
     */
    @Size(
            max = 300,
            message = "La descripción no puede superar los 300 caracteres"
    )
    private String descripcion; 
}
