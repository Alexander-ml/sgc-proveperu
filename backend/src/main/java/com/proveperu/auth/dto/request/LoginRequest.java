package com.proveperu.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "El usuario es obligatorio")
    private String usuarioLogin;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
