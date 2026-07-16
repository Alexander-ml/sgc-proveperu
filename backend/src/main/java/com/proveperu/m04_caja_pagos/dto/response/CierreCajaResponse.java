package com.proveperu.m04_caja_pagos.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CierreCajaResponse {
   private Integer idCierreCaja;
    private Integer idAperturaCaja;
    private Integer idCaja;
    private String nombreCaja;
    private BigDecimal saldoTeorico;
    private BigDecimal saldoReal;
    private BigDecimal diferencia;
    private LocalDateTime fechaHoraCierre;
    private String cerradaPor;
    private String estadoCaja;  
}
