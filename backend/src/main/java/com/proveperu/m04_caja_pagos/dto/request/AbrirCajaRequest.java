package com.proveperu.m04_caja_pagos.dto.request;
import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbrirCajaRequest {
     @NotNull(message = "El monto inicial es obligatorio")
    @DecimalMin(value = "0.00", inclusive = true, message = "El monto inicial no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El monto inicial debe tener como máximo 10 enteros y 2 decimales")
    private BigDecimal montoInicial;
}
