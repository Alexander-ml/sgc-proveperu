package com.proveperu.m05_gestion_clientes.dto.response;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para mostrar el historial completo
 * de compras de un cliente.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteHistorialDetalleResponse {
   
    /**
     * Identificador técnico del cliente.
     */
    private Integer idCliente;

    /**
     * Nombre completo o razón social del cliente.
     */
    private String nombreCliente;

    /**
     * Iniciales utilizadas en el avatar.
     */
    private String iniciales;

    /**
     * Tipo de documento del cliente.
     *
     * Valores: DNI o RUC.
     */
    private String tipoDocumento;

    /**
     * Número de documento del cliente.
     */
    private String numeroDocumento;

    /**
     * Cantidad total de compras realizadas.
     */
    private Integer numeroCompras;

    /**
     * Monto total gastado por el cliente.
     */
    private BigDecimal montoTotal;

    /**
     * Importe promedio por compra.
     */
    private BigDecimal ticketPromedio;

    /**
     * Compras realizadas por el cliente,
     * ordenadas desde la más reciente.
     */
    @Builder.Default
    private List<CompraHistorialResponse> compras =
            new ArrayList<>(); 
}
