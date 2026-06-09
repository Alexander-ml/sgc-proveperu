package com.proveperu.m06_usuarios.dto.response;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PermisoListadoResponse {
        private Integer idPermiso;

    private String modulo;

    private String accion;

    private String descripcion;

    private String estado;
}
