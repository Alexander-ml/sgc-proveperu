package com.proveperu.m02_inventario.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de respuesta para los indicadores principales
 * del módulo de inventario.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioDashboardResponse {
    
    private Integer totalProductos;
    private Integer productosSinStock;
    private Integer productosStockBajo;
    private Integer productosDisponibles;
}
