package com.proveperu.m05_gestion_clientes.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Respuesta utilizada para listar clientes en vista tabla
 * o vista tarjeta.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteListadoResponse {
    private Integer idCliente;

    private String nombreCliente;

    private String iniciales;

    private String tipoCliente;

    private String tipoDocumento;

    private String numeroDocumento;

    private String telefono;

    private String direccion;

    private String estado;

    private Integer numeroCompras;

    private BigDecimal montoTotal;

    private LocalDateTime ultimaCompra; 
}
