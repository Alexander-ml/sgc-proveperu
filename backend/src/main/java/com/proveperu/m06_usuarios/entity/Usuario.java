package com.proveperu.m06_usuarios.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un usuario autenticable dentro del sistema.
 *
 * El usuario posee credenciales de acceso,
 * pertenece a un rol y puede participar
 * en procesos de auditoría.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuario")
public class Usuario  {

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

    @Column(name = "estado_logico", nullable = false)
    private Integer estadoLogico = 1; // 1 - 0

    /**
     * Estado operativo del usuario.
     */
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private String estadoFisico = "ACTIVO"; // "ACTIVO - SUSPENDIDO"

    @Column(name = "fecha_hora_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaHoraCreacion;

    @Column(name = "fecha_hora_actualizacion")
    private LocalDateTime fechaHoraActualizacion;

    /**
     * Usuario que creó este registro.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "id_usuario_creador")
    private Integer idUsuarioCreador;

    /**
     * Usuario que realizó la última actualización.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "id_usuario_actualizo")
    private Integer idUsuarioActualizo;

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
}
