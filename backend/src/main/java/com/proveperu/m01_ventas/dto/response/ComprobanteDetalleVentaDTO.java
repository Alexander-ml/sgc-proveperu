package com.proveperu.m01_ventas.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO del comprobante emitido en el contexto del detalle de venta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobanteDetalleVentaDTO {
    /** Tipo documental: BOLETA, FACTURA o NOTA. */
    private String tipoComprobante;

    /** Serie del comprobante. */
    private String serie;

    /** Número correlativo. */
    private String correlativo;

    /** Fecha y hora de emisión del comprobante. */
    private LocalDateTime fechaEmision;
}
