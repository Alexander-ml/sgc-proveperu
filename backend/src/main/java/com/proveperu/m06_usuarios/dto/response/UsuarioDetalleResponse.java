package com.proveperu.m06_usuarios.dto.response;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsuarioDetalleResponse {
     private Integer idUsuario;

    private String nombreCompleto;

    private String usuarioLogin;

    private Integer idRol;

    private String rol;

    private String estado;
}
