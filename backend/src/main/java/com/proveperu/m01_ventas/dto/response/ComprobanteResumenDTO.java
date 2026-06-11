package com.proveperu.m01_ventas.dto.response;

import lombok.*;

/**
 * DTO resumido del comprobante asociado a una venta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobanteResumenDTO {
    private String tipoComprobante;
    private String serie;
    private String correlativo;
}
