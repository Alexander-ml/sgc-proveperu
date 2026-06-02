package com.proveperu.m06_usuarios.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre_completo", nullable = false, length = 100)
    private String nombreCompleto;

    @Column(name = "usuario_login", nullable = false, unique = true, length = 255)
    private String usuarioLogin;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(name = "estado_logico", nullable = false)
    private Integer estadoLogico = 1; // 1 - 0

    @Column(name = "estado_fisico", nullable = false, length = 20)
    private String estadoFisico = "ACTIVO"; // "ACTIVO - SUSPENDIDO"

    @Column(name = "fecha_hora_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaHoraCreacion;

    @Column(name = "fecha_hora_actualizacion")
    private LocalDateTime fechaHoraActualizacion;

    @Column(name = "id_usuario_creador")
    private Integer idUsuarioCreador;

    @Column(name = "id_usuario_actualizo")
    private Integer idUsuarioActualizo;

}
