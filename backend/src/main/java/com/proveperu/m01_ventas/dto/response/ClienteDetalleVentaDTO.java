package com.proveperu.m01_ventas.dto.response;

import lombok.*;

/**
 * DTO resumido del cliente en el contexto del detalle de venta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDetalleVentaDTO {
    /** Identificador técnico del cliente. */
    private Integer idCliente;

    /**
     * Nombre visible:
     * PERSONA - nombre_completo
     * EMPRESA - razon_social
     */
    private String nombreVisible;
}
