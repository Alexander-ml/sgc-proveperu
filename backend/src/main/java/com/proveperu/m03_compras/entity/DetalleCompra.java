package com.proveperu.m03_compras.entity;

import com.proveperu.m02_inventario.entity.Producto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Representa un producto incluido dentro de una compra.
 *
 * <p>
 * Cada registro almacena la cantidad adquirida,
 * el precio unitario de compra y el subtotal
 * correspondiente.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalle_compra")
public class DetalleCompra {
    /**
     * Identificador compuesto del detalle.
     */
    @EmbeddedId
    private DetalleCompraId id;

    /**
     * Compra asociada al detalle.
     *
     * <p>
     * La relación es administrada mediante la clave
     * foránea {@code id_compra}.
     * </p>
     */
    @MapsId("idCompra")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra", nullable = false)
    private Compra compra;

    /**
     * Producto asociado al detalle.
     *
     * <p>
     * La relación es administrada mediante la clave
     * foránea {@code id_producto}.
     * </p>
     */
    @MapsId("idProducto")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    /**
     * Cantidad adquirida.
     */
    @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    /**
     * Precio unitario de compra.
     */
    @Column(name = "precio_unitario_compra", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitarioCompra;

    /**
     * Importe subtotal del detalle.
     */
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
}
