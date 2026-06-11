package com.proveperu.m01_ventas.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResumenDTO {
    private Integer idCliente;

    /**
     * Para PERSONA → nombre_completo
     * Para EMPRESA  → razon_social
     */
    private String nombreVisible;
}
