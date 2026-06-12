package com.proveperu.m06_usuarios.entity;

import com.proveperu.m02_inventario.entity.MovimientoInventario;
import com.proveperu.m04_caja_pagos.entity.AperturaCaja;
import com.proveperu.m06_usuarios.enums.EstadoUsuario;
import com.proveperu.shared.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa a un usuario del sistema.
 *
 * Contiene la información necesaria para autenticación,
 * autorización y auditoría operativa.
 *
 * Hereda de {@link BaseAuditEntity}, incorporando información
 * de auditoría como estado lógico, fecha de creación y fecha
 * de última actualización.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuario")
public class Usuario  extends BaseAuditEntity {

    /**
     * Identificador único del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    /**
     * Nombre completo del usuario.
     */
    @Column(name = "nombre_completo", nullable = false, length = 100)
    private String nombreCompleto;

    /**
     * Correo utilizado para autenticación.
     */
    @Column(name = "usuario_login", nullable = false, unique = true, length = 255)
    private String usuarioLogin;

    /**
     * Contraseña cifrada.
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * Rol asignado al usuario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    /**
     * Estado físico del usuario.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoUsuario estadoUsuario;

    /**
     * Usuario que creó el registro.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_creador")
    private Usuario usuarioCreador;

    /**
     * Usuario que realizó la última actualización.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_actualizo")
    private Usuario usuarioActualizo;

    /**
     * Historial de sesiones iniciadas por el usuario.
     *
     * La relación es administrada por la entidad
     * {@link UsuarioSesion} mediante el atributo
     * {@code usuario}.
     */
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @Builder.Default
    private List<UsuarioSesion> usuarioSesiones = new ArrayList<>();

    /**
     * Movimientos de inventario registrados
     * por el usuario.
     *
     * La relación es administrada por
     * {@link MovimientoInventario} mediante el atributo
     * {@code usuarioRegistro}.
     */
    @OneToMany(mappedBy = "usuarioRegistro", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MovimientoInventario> movimientosInventario = new ArrayList<>();

    /**
     * Aperturas de caja registradas por el usuario.
     *
     * <p>
     * La relación es administrada por la entidad
     * {@link AperturaCaja} mediante el atributo
     * {@code usuarioRegistro}.
     * </p>
     */
    @OneToMany(
            mappedBy = "usuarioRegistro",
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<AperturaCaja> aperturasCaja = new ArrayList<>();
}
