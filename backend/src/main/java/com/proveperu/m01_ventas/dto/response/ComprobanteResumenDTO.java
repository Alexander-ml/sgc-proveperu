package com.proveperu.m01_ventas.dto.response;

import lombok.*;

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
