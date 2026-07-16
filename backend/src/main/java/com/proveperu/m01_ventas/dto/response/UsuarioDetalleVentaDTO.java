package com.proveperu.m01_ventas.dto.response;

import lombok.*;

/**
 * DTO resumido del usuario vendedor en el contexto del detalle de venta.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDetalleVentaDTO {
    /** Identificador técnico del usuario. */
    private Integer idUsuario;

    /** Nombre completo del usuario. */
    private String nombreCompleto;
}
