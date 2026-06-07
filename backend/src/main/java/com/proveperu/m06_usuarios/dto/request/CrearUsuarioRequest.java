package com.proveperu.m06_usuarios.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CrearUsuarioRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombreCompleto;

    @Email(message = "Correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    private String usuarioLogin;

    @NotNull(message = "El rol es obligatorio")
    private Integer idRol;

    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    private String password;
}
