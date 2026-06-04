package com.proveperu.m02_inventario.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento_inventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento_inventario")
    private Integer idMovimientoInventario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_movimiento_inventario", nullable = false)
    private TipoMovimientoInventario tipoMovimientoInventario;

    @Column(name = "id_usuario_registro", nullable = false)
    private Integer idUsuarioRegistro;

    @Column(name = "id_venta")
    private Integer idVenta;

    @Column(name = "id_compra")
    private Integer idCompra;

    @Column(name = "id_recepcion_compra")
    private Integer idRecepcionCompra;

    @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "stock_anterior", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockAnterior;

    @Column(name = "stock_nuevo", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockNuevo;

    @Column(name = "fecha_hora_movimiento_inventario", nullable = false)
    private LocalDateTime fechaHoraMovimientoInventario;

    @Column(name = "estado_fisico", nullable = false, length = 20)
    private String estadoFisico = "REGISTRADO";
}