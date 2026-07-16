package com.proveperu.m04_caja_pagos.entity;

import com.proveperu.m06_usuarios.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa el cierre de una caja operativa.
 *
 * Registra los montos finales calculados por el sistema,
 * los montos reales contabilizados y la diferencia resultante
 * al finalizar una sesión de caja.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cierre_caja")
public class CierreCaja {
    /**
     * Identificador único del cierre de caja.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cierre_caja")
    private Integer idCierreCaja;

    /**
     * Apertura asociada al cierre.
     *
     * Una apertura solamente puede tener un cierre.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_apertura_caja", nullable = false, unique = true)
    private AperturaCaja aperturaCaja;

    /**
     * Usuario responsable del registro del cierre.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_registro", nullable = false)
    private Usuario usuarioRegistro;

    /**
     * Saldo esperado calculado por el sistema.
     */
    @Column(name = "saldo_teorico", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoTeorico;

    /**
     * Saldo contabilizado físicamente.
     */
    @Column(name = "saldo_real", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoReal;

    /**
     * Diferencia entre el saldo teórico y el saldo real.
     */
    @Column(name = "diferencia", nullable = false, precision = 12, scale = 2)
    private BigDecimal diferencia;

    /**
     * Fecha y hora en que se realizó el cierre.
     */
    @Column(name = "fecha_hora_cierre", nullable = false)
    private LocalDateTime fechaHoraCierre;
}
