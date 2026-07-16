package com.proveperu.m02_inventario.dto.request;

import java.math.BigDecimal;

import com.proveperu.m02_inventario.enums.UnidadMedidaProducto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de entrada para registrar un nuevo producto
 * dentro del módulo de inventario.
 */
@Getter
@Setter
public class RegistrarProductoRequest {
    
    @NotBlank(message = "El código del producto es obligatorio")
    @Size(max = 50, message = "El código del producto no puede superar los 50 caracteres")
    @Pattern(
            regexp = "^[A-Za-z0-9_-]+$",
            message = "El código del producto no debe contener espacios ni caracteres inválidos"
    )
    private String codigoProducto;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre del producto no puede superar los 100 caracteres")
    private String nombreProducto;

    @Size(max = 300, message = "La descripción no puede superar los 300 caracteres")
    private String descripcion;

    @NotNull(message = "La unidad de medida es obligatoria")
    private UnidadMedidaProducto unidadMedida;

    @NotNull(message = "La cantidad inicial es obligatoria")
    @DecimalMin(value = "0.00", inclusive = true, message = "La cantidad inicial no puede ser negativa")
    @Digits(integer = 8, fraction = 2, message = "La cantidad inicial debe tener como máximo 8 enteros y 2 decimales")
    private BigDecimal cantidadInicial;

    @NotNull(message = "El stock mínimo es obligatorio")
    @DecimalMin(value = "0.00", inclusive = true, message = "El stock mínimo no puede ser negativo")
    @Digits(integer = 8, fraction = 2, message = "El stock mínimo debe tener como máximo 8 enteros y 2 decimales")
    private BigDecimal stockMinimo;
}
