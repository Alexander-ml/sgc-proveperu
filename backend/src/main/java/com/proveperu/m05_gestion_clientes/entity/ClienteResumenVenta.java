package com.proveperu.m05_gestion_clientes.entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Entidad de solo lectura que representa la vista
 * vw_resumen_ventas_cliente de la base de datos.
 *
 * Contiene las estadísticas de ventas utilizadas
 * por el módulo de gestión de clientes.
 */
@Getter
@NoArgsConstructor
@Immutable
@Entity
@Table(name = "vw_resumen_ventas_cliente")
public class ClienteResumenVenta {
    
    /**
     * Identificador del cliente.
     * También funciona como identificador único de cada fila de la vista.
     */
    @Id
    @Column(name = "id_cliente")
    private Integer idCliente;

    /**
     * Cantidad de ventas registradas del cliente.
     */
    @Column(name = "numero_compras")
    private Long numeroCompras;

    /**
     * Monto total acumulado de las ventas registradas.
     */
    @Column(name = "monto_total")
    private BigDecimal montoTotal;

    /**
     * Promedio del valor de las compras realizadas.
     */
    @Column(name = "ticket_promedio")
    private BigDecimal ticketPromedio;

    /**
     * Fecha y hora de la venta más reciente.
     */
    @Column(name = "ultima_compra")
    private LocalDateTime ultimaCompra;
}
