package com.proveperu.m01_ventas.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoPagoVentaResumenDTO {
    private Integer idMetodoPago;
    private String nombreMetodoPago;
    private BigDecimal monto;
}
