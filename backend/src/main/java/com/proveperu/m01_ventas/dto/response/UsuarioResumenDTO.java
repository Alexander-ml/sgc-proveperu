package com.proveperu.m01_ventas.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResumenDTO {
    private Integer idUsuario;
    private String nombreCompleto;
}
