package com.proveperu.m06_usuarios.dto.response;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RolListadoResponse {
       private Integer idRol;

    private String nombreRol;

    private String descripcion;

    private String estado;

    private Integer cantidadPermisos;
}
