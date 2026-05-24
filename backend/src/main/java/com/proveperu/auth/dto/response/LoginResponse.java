package com.proveperu.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponse {
    private String token; // JWT generado
    private String tipo;  // Tipo de autenticación ("Bearer")
    private String usuarioLogin; // Identificador
    private String nombreCompleto; // Información de usuario
    private String rol; // Autorización
    private long expiresIn; // Tiempo de expiración
}
