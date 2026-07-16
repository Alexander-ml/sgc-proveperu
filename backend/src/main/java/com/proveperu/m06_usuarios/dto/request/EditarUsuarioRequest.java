package com.proveperu.m06_usuarios.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditarUsuarioRequest {
     @NotBlank(message = "El nombre es obligatorio")
    private String nombreCompleto;

    @Email(message = "Correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    private String usuarioLogin;

    @NotBlank(message = "El rol es obligatorio")
    private String nombreRol;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
