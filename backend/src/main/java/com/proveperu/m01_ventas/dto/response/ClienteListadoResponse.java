package com.proveperu.m01_ventas.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO de respuesta que representa un cliente disponible
 * para ser asignado a una nueva venta.
 *
 * <p>
 * Solo expone los datos mínimos necesarios para que el
 * frontend muestre la lista de selección de clientes.
 * No expone la entidad {@code Cliente} directamente.
 * </p>
 *
 * <p>
 * Se utiliza en el endpoint {@code GET /ventas/clientes}.
 * </p>
 */
@Getter
@Builder
public class ClienteListadoResponse {

    /**
     * Identificador único del cliente.
     */
    private Integer idCliente;

    /**
     * Tipo de cliente: "PERSONA" o "EMPRESA".
     */
    private String tipoCliente;

    /**
     * Nombre completo del cliente.
     * Para personas: nombre_completo.
     * Para empresas: razón social.
     */
    private String nombreCompleto;

    /**
     * RUC del cliente (solo aplica a empresas).
     * Puede ser nulo para personas naturales.
     */
    private String ruc;

    /**
     * Número de teléfono de contacto del cliente.
     * Puede ser nulo si no fue registrado.
     */
    private String telefono;
}