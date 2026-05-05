package com.proveperu.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponse {
    private String token;
    private String tipo;           // "Bearer"
    private String usuarioLogin;
    private String nombreCompleto;
    private String rol;
    private long expiresIn;        // milisegundos hasta expiración
}
