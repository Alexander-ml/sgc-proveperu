package com.proveperu.m02_inventario.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de respuesta para mostrar productos
 * dentro de la tabla principal de inventario.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoInventarioResponse {
    
    private Integer idProducto;
    private String codigoProducto;
    private String nombreProducto;
    private String descripcion;
    private String unidadMedida;
    private BigDecimal cantidadActual;
    private BigDecimal stockMinimo;
    private String estadoStock;
    private String estadoProducto;
    private LocalDateTime fechaHoraActualizacion;
}
