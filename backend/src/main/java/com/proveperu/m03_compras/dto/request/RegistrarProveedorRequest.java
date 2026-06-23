package com.proveperu.m03_compras.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para registrar un proveedor
 * dentro del módulo de compras.
 */
@Getter
@Setter
public class RegistrarProveedorRequest {
    
    @NotBlank(message = "El RUC es obligatorio")
    @Pattern(
            regexp = "\\d{11}",
            message = "El RUC debe tener 11 dígitos"
    )
    private String ruc;

    @NotBlank(message = "La razón social es obligatoria")
   @Size(max = 100, message = "La razón social no debe superar los 100 caracteres")
   private String razonSocial;

    @Size(max = 20, message = "El teléfono no debe superar los 20 caracteres")
    private String telefono;

    @Size(max = 200, message = "La dirección no debe superar los 200 caracteres")
    private String direccion;
}
