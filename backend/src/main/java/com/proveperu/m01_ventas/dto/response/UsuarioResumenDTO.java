package com.proveperu.m01_ventas.dto.response;

import lombok.*;

/**
 * DTO resumido del usuario vendedor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResumenDTO {
    private Integer idUsuario;
    private String nombreCompleto;
}
