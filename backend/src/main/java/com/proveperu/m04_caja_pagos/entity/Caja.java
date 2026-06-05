package com.proveperu.m04_caja_pagos.entity;

import com.proveperu.m04_caja_pagos.enums.EstadoCaja;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una caja operativa dentro del sistema.
 *
 * <p>
 * Una caja almacena el saldo disponible y controla
 * su disponibilidad para registrar movimientos financieros.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "caja")
public class Caja {
    /**
     * Identificador único de la caja.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caja")
    private Integer idCaja;

    /**
     * Nombre descriptivo de la caja.
     */
    @Column(name = "nombre_caja", nullable = false, unique = true, length = 50)
    private String nombreCaja;

    /**
     * Saldo monetario actual disponible en la caja.
     */
    @Column(name = "saldo_actual", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoActual;

    /**
     * Estado operativo actual de la caja.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoCaja estadoFisico;

    /**
     * Fecha y hora de creación de la caja.
     */
    @Column(name = "fecha_hora_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaHoraCreacion;

    /**
     * Historial de aperturas registradas para la caja.
     *
     * <p>
     * La relación es administrada por la entidad
     * {@link AperturaCaja} mediante el atributo
     * {@code caja}.
     * </p>
     *
     * <p>
     * Una caja puede tener múltiples aperturas
     * registradas a lo largo de su ciclo operativo.
     * </p>
     */
    @OneToMany(mappedBy = "caja", fetch = FetchType.LAZY)
    @Builder.Default
    private List<AperturaCaja> aperturas = new ArrayList<>();
}
