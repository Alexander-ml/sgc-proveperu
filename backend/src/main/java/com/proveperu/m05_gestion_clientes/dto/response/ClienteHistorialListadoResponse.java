package com.proveperu.m05_gestion_clientes.dto.response;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para mostrar un cliente dentro
 * del listado del historial de compras.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteHistorialListadoResponse {
     /**
     * Identificador técnico del cliente.
     */
    private Integer idCliente;

    /**
     * Nombre completo o razón social.
     */
    private String nombreCliente;

    /**
     * Iniciales utilizadas en el avatar.
     */
    private String iniciales;

    /**
     * Cantidad de compras registradas.
     */
    private Integer numeroCompras;

    /**
     * Monto total gastado por el cliente.
     */
    private BigDecimal montoTotal;
}
