package com.proveperu.m05_gestion_clientes.dto.request;
import com.proveperu.m05_gestion_clientes.enums.TipoCliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para registrar un nuevo cliente.
 */
@Getter
@Setter
public class RegistrarClienteRequest {
    
    /**
     * Tipo de cliente: PERSONA o EMPRESA.
     */
    @NotNull(message = "Debe seleccionar el tipo de cliente")
    private TipoCliente tipoCliente;

    /**
     * Nombre completo de la persona o razón social de la empresa.
     */
    @NotBlank(message = "El nombre o razón social es obligatorio")
    @Size(
            max = 150,
            message = "El nombre o razón social no puede superar los 150 caracteres"
    )
    private String nombreCliente;

    /**
     * DNI para personas o RUC para empresas.
     *
     * La longitud exacta se validará en el servicio según
     * el tipo de cliente seleccionado.
     */
    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(
            regexp = "\\d+",
            message = "El número de documento solo debe contener dígitos"
    )
    private String numeroDocumento;

    /**
     * Número telefónico de contacto.
     */
    @Size(
            max = 20,
            message = "El teléfono no puede superar los 20 caracteres"
    )
    private String telefono;

    /**
     * Dirección física del cliente.
     */
    @Size(
            max = 200,
            message = "La dirección no puede superar los 200 caracteres"
    )
    private String direccion;
}
