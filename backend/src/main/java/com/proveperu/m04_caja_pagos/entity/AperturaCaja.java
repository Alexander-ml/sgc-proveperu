package com.proveperu.m04_caja_pagos.entity;

import com.proveperu.m06_usuarios.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa la apertura de una caja dentro del sistema.
 *
 * <p>
 * Cada registro almacena el monto inicial con el que
 * una caja comienza su operación diaria y el usuario
 * responsable de dicha apertura.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "apertura_caja")
public class AperturaCaja {
    /**
     * Identificador único de la apertura.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_apertura_caja")
    private Integer idAperturaCaja;

    /**
     * Caja que fue abierta.
     *
     * <p>
     * Muchas aperturas pueden pertenecer a una misma caja.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caja", nullable = false)
    private Caja caja;

    /**
     * Usuario responsable del registro de apertura.
     *
     * <p>
     * Un usuario puede registrar múltiples aperturas.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_registro", nullable = false)
    private Usuario usuarioRegistro;

    /**
     * Monto inicial con el que se apertura la caja.
     */
    @Column(name = "monto_inicial", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoInicial;

    /**
     * Fecha y hora en que se realizó la apertura.
     */
    @Column(name = "fecha_hora_apertura", nullable = false, updatable = false)
    private LocalDateTime fechaHoraApertura;

    /**
     * Cierre asociado a la apertura.
     */
    @OneToOne(mappedBy = "aperturaCaja", fetch = FetchType.LAZY)
    private CierreCaja cierreCaja;
}
