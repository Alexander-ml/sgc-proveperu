package com.proveperu.m01_ventas.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO de respuesta que representa un método de pago
 * disponible para registrar en una venta.
 *
 * <p>
 * Contiene únicamente los campos necesarios para la
 * selección en el frontend. No expone la entidad
 * {@code MetodoPago} directamente.
 * </p>
 *
 * <p>
 * Se utiliza en el endpoint {@code GET /ventas/metodos-pago}.
 * </p>
 */
@Getter
@Builder
public class MetodoPagoResponse {

    /**
     * Identificador único del método de pago.
     */
    private Integer idMetodoPago;

    /**
     * Nombre descriptivo del método de pago.
     * Ejemplos: "EFECTIVO", "TRANSFERENCIA", "YAPE", "POS".
     */
    private String nombre;
}