package com.proveperu.m01_ventas.dto.response;

import lombok.*;

/**
 * DTO resumido del cliente asociado a una venta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResumenDTO {

    /** Identificador del cliente. */
    private Integer idCliente;

    /**
     * Nombre visible del cliente:
     *  PERSONA - nombre_completo
     *  EMPRESA - razon_social
     */
    private String nombreCliente;
}
