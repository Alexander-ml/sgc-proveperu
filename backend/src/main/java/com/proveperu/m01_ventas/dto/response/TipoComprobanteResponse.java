package com.proveperu.m01_ventas.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO de respuesta que representa un tipo de comprobante
 * disponible para emitir en una venta.
 *
 * <p>
 * Se construye a partir del enum {@code TipoComprobante}
 * y no requiere acceso a base de datos.
 * </p>
 *
 * <p>
 * Se utiliza en el endpoint {@code GET /ventas/tipo-comprobantes}.
 * </p>
 */
@Getter
@Builder
public class TipoComprobanteResponse {

    /**
     * Código del comprobante tal como se define
     * en el enum TipoComprobante.
     * Ejemplos: "BOLETA", "FACTURA", "NOTA".
     */
    private String codigo;

    /**
     * Descripción legible del tipo de comprobante
     * para mostrar en interfaz de usuario.
     */
    private String descripcion;
}