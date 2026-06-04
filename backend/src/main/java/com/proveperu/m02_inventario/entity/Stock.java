package com.proveperu.m02_inventario.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa el stock disponible de un producto dentro del inventario.
 *
 * <p>
 * Mantiene la cantidad actual disponible y el stock mínimo
 * requerido para generar alertas de reposición.
 * </p>
 *
 * <p>
 * Implementa una relación One-To-One con Producto mediante
 * clave primaria compartida.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stock")
public class Stock {

    /**
     * Identificador del producto.
     *
     * <p>
     * Corresponde simultáneamente a:
     * </p>
     * <ul>
     *     <li>Clave primaria de Stock.</li>
     *     <li>Clave foránea hacia Producto.</li>
     * </ul>
     */
    @Id
    @Column(name = "id_producto")
    private Integer idProducto;

    /**
     * Producto asociado al stock.
     * <p>
     * Relación One-To-One con clave primaria compartida.
     * </p>
     */
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    /**
     * Cantidad disponible actualmente.
     */
    @Column(name = "cantidad_actual", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidadActual = BigDecimal.ZERO;

    /**
     * Stock mínimo permitido antes de requerir reposición.
     */
    @Column(name = "stock_minimo", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockMinimo = BigDecimal.ZERO;

    /**
     * Fecha y hora de la última actualización del stock.
     */
    @Column(name = "fecha_hora_actualizacion", nullable = false)
    private LocalDateTime fechaHoraActualizacion;
}
