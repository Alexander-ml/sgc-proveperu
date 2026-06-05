package com.proveperu.m02_inventario.entity;

import com.proveperu.shared.entity.BaseEstadoLogicoEntity;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Catálogo maestro de tipos de movimientos
 * utilizados dentro del inventario.
 *
 * <p>
 * Define la naturaleza del movimiento que
 * afecta las existencias de los productos.
 * </p>
 *
 * <ul>
 *     <li>INGRESO</li>
 *     <li>EGRESO</li>
 *     <li>AJUSTE_POSITIVO</li>
 *     <li>AJUSTE_NEGATIVO</li>
 * </ul>
 *
 * <p>
 * Hereda de {@link BaseEstadoLogicoEntity}, incorporando estado lógico.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tipo_movimiento_inventario")
public class TipoMovimientoInventario extends BaseEstadoLogicoEntity {

    /**
     * Identificador único del tipo de movimiento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_movimiento_inventario")
    private Integer idTipoMovimientoInventario;

    /**
     * Tipo funcional del movimiento.
     */
    @Column(name = "nombre", nullable = false, unique = true, length = 30)
    private String nombre;

    /**
     * Estado operativo del registro.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoActivoInactivo estadoFisico;

    /**
     * Movimientos asociados a este tipo.
     *
     * La relación es administrada por
     * {@link MovimientoInventario} mediante el atributo
     * {@code tipoMovimientoInventario}.
     */
    @OneToMany(mappedBy = "tipoMovimientoInventario", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MovimientoInventario> movimientosInventario = new ArrayList<>();
}