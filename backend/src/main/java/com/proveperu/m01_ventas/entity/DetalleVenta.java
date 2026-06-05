package com.proveperu.m01_ventas.entity;

import com.proveperu.m02_inventario.entity.Producto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Representa una línea de detalle dentro de una venta.
 *
 * <p>
 * Cada registro identifica un producto vendido
 * dentro de una venta específica.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {
    /**
     * Clave primaria compuesta.
     */
    @EmbeddedId
    private DetalleVentaId id;

    /**
     * Venta propietaria del detalle.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idVenta")
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    /**
     * Producto vendido.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProducto")
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    /**
     * Cantidad vendida.
     */
    @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    /**
     * Precio unitario aplicado al momento
     * de la venta.
     */
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    /**
     * Subtotal calculado para la línea.
     */
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
}
