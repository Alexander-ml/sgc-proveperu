package com.proveperu.m04_caja_pagos.entity;

import com.proveperu.m04_caja_pagos.enums.NombreTipoMovimientoCaja;
import jakarta.persistence.*;
import lombok.*;

/**
 * Catálogo maestro que define los tipos de movimiento
 * permitidos dentro del módulo de caja.
 *
 * Los valores posibles son:
 * <ul>
 *     <li>INGRESO</li>
 *     <li>EGRESO</li>
 * </ul>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tipo_movimiento_caja")
public class TipoMovimientoCaja {
    /**
     * Identificador único del tipo de movimiento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_movimiento_caja")
    private Integer idTipoMovimientoCaja;

    /**
     * Tipo funcional del movimiento de caja.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "nombre_tipo_movimiento", nullable = false, length = 20, unique = true)
    private NombreTipoMovimientoCaja nombreTipoMovimiento;
}
