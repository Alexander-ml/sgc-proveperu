package com.proveperu.m02_inventario.entity;

import com.proveperu.m01_ventas.entity.Venta;
import com.proveperu.m02_inventario.enums.EstadoMovimientoInventario;
import com.proveperu.m03_compras.entity.Compra;
import com.proveperu.m03_compras.entity.RecepcionCompra;
import com.proveperu.m06_usuarios.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa un movimiento realizado sobre
 * el inventario de un producto.
 *
 * <p>
 * Permite registrar entradas, salidas y ajustes
 * de stock manteniendo trazabilidad histórica.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimiento_inventario")
public class MovimientoInventario {

    /**
     * Identificador único del movimiento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento_inventario")
    private Integer idMovimientoInventario;

    /**
     * Producto afectado por el movimiento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    /**
     * Tipo funcional del movimiento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_movimiento_inventario", nullable = false)
    private TipoMovimientoInventario tipoMovimientoInventario;

    /**
     * Usuario que registró el movimiento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_registro", nullable = false)
    private Usuario usuarioRegistro;

    /**
     * Venta asociada al movimiento.
     *
     * Se utiliza cuando el movimiento
     * corresponde a una salida de inventario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta")
    private Venta venta;

    /**
     * Compra asociada al movimiento.
     *
     * Se utiliza cuando el movimiento
     * proviene de un proceso de compra.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra")
    private Compra compra;

    /**
     * Recepción de compra asociada.
     *
     * Se utiliza cuando el stock ingresa
     * físicamente al almacén.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recepcion_compra")
    private RecepcionCompra recepcionCompra;

    /**
     * Cantidad movilizada.
     */
    @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    /**
     * Stock existente antes del movimiento.
     */
    @Column(name = "stock_anterior", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockAnterior;

    /**
     * Stock resultante después del movimiento.
     */
    @Column(name = "stock_nuevo", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockNuevo;

    /**
     * Fecha y hora de registro del movimiento.
     */
    @Column(name = "fecha_hora_movimiento_inventario", nullable = false)
    private LocalDateTime fechaHoraMovimientoInventario;

    /**
     * Estado operativo del movimiento.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoMovimientoInventario estadoFisico;
}