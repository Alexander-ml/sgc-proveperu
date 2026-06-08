package com.proveperu.m06_usuarios.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditarUsuarioRequest {
        @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @Email(message = "Correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    private String usuarioLogin;

    @NotNull(message = "Debe seleccionar un rol")
    private Integer idRol;

    @NotBlank(message = "Debe indicar el estado")
    private String estado;
}
