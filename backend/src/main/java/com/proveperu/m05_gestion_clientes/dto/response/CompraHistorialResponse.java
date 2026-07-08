package com.proveperu.m05_gestion_clientes.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa una compra realizada por un cliente
 * dentro de su historial.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraHistorialResponse {
   
    /**
     * Identificador técnico de la venta.
     */
    private Integer idVenta;

    /**
     * Código visual de la venta.
     *
     * Ejemplo: V-2026-000003.
     */
    private String codigoVenta;

    /**
     * Fecha y hora en que se registró la venta.
     */
    private LocalDateTime fechaHoraVenta;

    /**
     * Estado real de la venta.
     *
     * Valores posibles: REGISTRADA o ANULADA.
     */
    private String estadoVenta;

    /**
     * Importe total de la venta.
     */
    private BigDecimal total;

    /**
     * Método o métodos utilizados para pagar.
     *
     * Ejemplos:
     * Yape
     * Efectivo
     * Efectivo + Yape
     */
    private String metodoPago;

    /**
     * Tipo de comprobante emitido.
     *
     * Ejemplos: BOLETA, FACTURA o NOTA.
     */
    private String tipoComprobante;

    /**
     * Serie y correlativo del comprobante.
     *
     * Ejemplo: F001-00000044.
     */
    private String numeroComprobante;

    /**
     * Nombre del usuario que registró la venta.
     */
    private String atendidoPor;

    /**
     * Productos incluidos dentro de la venta.
     */
    @Builder.Default
    private List<ProductoHistorialCompraResponse> productos =
            new ArrayList<>(); 
}
