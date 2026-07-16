package com.proveperu.m03_compras.dto.response;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Respuesta con los indicadores principales
 * del módulo de compras.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraDashboardResponse {
     /**
     * Cantidad total de compras registradas,
     * sin considerar compras anuladas.
     */
    private Long totalCompras;

    /**
     * Suma total invertida en compras,
     * sin considerar compras anuladas.
     */
    private BigDecimal montoTotalInvertido;

    /**
     * Cantidad de compras recibidas.
     */
    private Long comprasRecibidas;

    /**
     * Cantidad de compras pendientes.
     */
    private Long comprasPendientes;
}
