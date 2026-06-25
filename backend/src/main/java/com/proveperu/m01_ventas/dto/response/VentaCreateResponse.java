package com.proveperu.m01_ventas.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta devuelto al confirmar exitosamente
 * una nueva venta.
 *
 * <p>
 * Contiene los datos mínimos de confirmación para que
 * el frontend pueda mostrar el resumen de la venta
 * creada: ID asignado, total, cambio a devolver y
 * el número de comprobante generado.
 * </p>
 *
 * <p>
 * Nunca expone las entidades JPA directamente.
 * Se utiliza en el endpoint {@code POST /ventas}.
 * </p>
 */
@Getter
@Builder
public class VentaCreateResponse {

    /**
     * Identificador único de la venta creada.
     */
    private Integer idVenta;

    /**
     * Fecha y hora en que fue registrada la venta.
     */
    private LocalDateTime fechaHoraVenta;

    /**
     * Total calculado de la venta (suma de subtotales).
     */
    private BigDecimal total;

    /**
     * Monto entregado por el cliente.
     */
    private BigDecimal montoPagado;

    /**
     * Cambio a devolver al cliente.
     * Calculado como {@code montoPagado - total}.
     * Será cero si el pago es exacto.
     */
    private BigDecimal cambioDevuelto;

    /**
     * Tipo de comprobante emitido.
     * Ejemplo: "BOLETA", "FACTURA", "NOTA".
     */
    private String tipoComprobante;

    /**
     * Serie del comprobante generado.
     */
    private String serieComprobante;

    /**
     * Correlativo del comprobante generado.
     */
    private String correlativoComprobante;

    /**
     * Mensaje informativo de confirmación.
     */
    private String mensaje;
}