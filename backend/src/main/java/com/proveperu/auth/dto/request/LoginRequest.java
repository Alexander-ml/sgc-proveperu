package com.proveperu.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de solicitud utilizado para iniciar el proceso de autenticación
 * de un usuario.
 */
@Getter
@Setter
public class LoginRequest {
    /**
     * Identificador de acceso utilizado por el usuario para autenticarse
     * en la plataforma.
     */
    @NotBlank(message = "El usuario es obligatorio")
    private String usuarioLogin;

    /**
     * Credencial secreta asociada a la cuenta del usuario.
     * */
    @NotBlank(message = "La contraseña es obligatoria") // Evita valores nulos o vacíos
    private String password;
}
