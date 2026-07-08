package com.proveperu.m05_gestion_clientes.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad de solo lectura que representa una venta
 * dentro del historial de compras de un cliente.
 *
 * La información proviene de la vista
 * vw_historial_compras_cliente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Table(name = "vw_historial_compras_cliente")
public class ClienteHistorialCompra {
    
    /**
     * Identificador de la venta.
     */
    @Id
    @Column(name = "id_venta")
    private Integer idVenta;

    /**
     * Identificador del cliente asociado.
     */
    @Column(name = "id_cliente")
    private Integer idCliente;

    /**
     * Código visual de la venta.
     *
     * Ejemplo: V-2026-000003.
     */
    @Column(name = "codigo_venta")
    private String codigoVenta;

    /**
     * Fecha y hora de la venta.
     */
    @Column(name = "fecha_hora_venta")
    private LocalDateTime fechaHoraVenta;

    /**
     * Estado de la venta.
     */
    @Column(name = "estado_venta")
    private String estadoVenta;

    /**
     * Importe total de la venta.
     */
    @Column(name = "total")
    private BigDecimal total;

    /**
     * Método o combinación de métodos de pago.
     */
    @Column(name = "metodo_pago")
    private String metodoPago;

    /**
     * Tipo de comprobante emitido.
     */
    @Column(name = "tipo_comprobante")
    private String tipoComprobante;

    /**
     * Serie y correlativo del comprobante.
     */
    @Column(name = "numero_comprobante")
    private String numeroComprobante;

    /**
     * Usuario que registró la venta.
     */
    @Column(name = "atendido_por")
    private String atendidoPor;
}
