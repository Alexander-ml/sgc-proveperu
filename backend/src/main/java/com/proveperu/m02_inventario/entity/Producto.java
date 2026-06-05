package com.proveperu.m02_inventario.entity;

import com.proveperu.m02_inventario.enums.UnidadMedidaProducto;
import com.proveperu.shared.entity.BaseAuditEntity;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un producto dentro del sistema de inventario.
 *
 * Define el catálogo base de productos utilizados en ventas, compras
 * y control de stock.
 *
 * Hereda de {@link BaseAuditEntity}, incorporando información
 * de auditoría como estado lógico, fecha de creación y fecha
 * de última actualización.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "producto")
public class Producto extends BaseAuditEntity {
    /**
     * Identificador único del producto.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    /**
     * Código único del producto.
     * No permite espacios según restricción de base de datos.
     */
    @Column(name = "codigo_producto", nullable = false, unique = true, length = 50)
    private String codigoProducto;

    /**
     * Nombre descriptivo del producto.
     */
    @Column(name = "nombre_producto", nullable = false, length = 100)
    private String nombreProducto;

    /**
     * Descripción detallada del producto.
     */
    @Column(name = "descripcion", length = 300)
    private String descripcion;

    /**
     * Unidad de medida del producto.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", nullable = false, length = 20)
    private UnidadMedidaProducto unidadMedida;

    /**
     * Estado físico del producto.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoActivoInactivo estadoFisico;

    /**
     * Movimientos registrados para el producto.
     *
     * La relación es administrada por
     * {@link MovimientoInventario}
     * mediante el atributo {@code producto}.
     */
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MovimientoInventario> movimientosInventario = new ArrayList<>();
}
