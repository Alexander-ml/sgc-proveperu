package com.proveperu.m06_usuarios.dto.response;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO utilizado para mostrar información resumida
 * de los usuarios en los listados del sistema.
 *
 * @author David Sanchez
 */
@Getter
@Builder
public class UsuarioListadoResponse {
    private Integer idUsuario;

    private String nombreCompleto;

    private String usuarioLogin;

    private String rol;

    private String estado;
}
