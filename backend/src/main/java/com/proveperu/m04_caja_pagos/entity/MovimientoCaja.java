package com.proveperu.m04_caja_pagos.entity;

import com.proveperu.m01_ventas.entity.Venta;
import com.proveperu.m03_compras.entity.Compra;
import com.proveperu.m04_caja_pagos.enums.EstadoMovimientoCaja;
import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.shared.entity.MetodoPago;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa un movimiento financiero que impacta el saldo
 * de una caja dentro del sistema.
 *
 * Puede originarse por:
 * <ul>
 *     <li>Ventas</li>
 *     <li>Compras</li>
 *     <li>Ajustes manuales</li>
 * </ul>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimiento_caja")
public class MovimientoCaja {
    /**
     * Identificador único del movimiento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento_caja")
    private Integer id;

    /**
     * Caja afectada.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caja", nullable = false)
    private Caja caja;

    /**
     * Tipo de movimiento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_movimiento_caja", nullable = false)
    private TipoMovimientoCaja tipoMovimientoCaja;

    /**
     * Usuario que registra el movimiento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_registra", nullable = false)
    private Usuario usuarioRegistra;

    /**
     * Venta asociada.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta")
    private Venta venta;

    /**
     * Compra asociada.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra")
    private Compra compra;

    /**
     * Método de pago utilizado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodo_pago")
    private MetodoPago metodoPago;

    /**
     * Importe del movimiento.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    /**
     * Observación del movimiento.
     */
    @Column(length = 300)
    private String descripcion;

    /**
     * Fecha y hora efectiva del movimiento.
     */
    @Column(name = "fecha_hora_movimiento", nullable = false)
    private LocalDateTime fechaHoraMovimiento;

    /**
     * Estado físico del movimiento.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoMovimientoCaja estadoFisico;
}
