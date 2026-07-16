package com.proveperu.m06_usuarios.dto.response;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RolDetalleResponse {
     private Integer idRol;

    private String nombreRol;

    private String descripcion;

    private String estado;

    private List<String> permisos;
}
