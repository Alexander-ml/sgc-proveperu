package com.proveperu.m06_usuarios.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad asociativa que representa la asignación
 * de permisos a un rol dentro del sistema.
 *
 * Permite almacenar información adicional sobre
 * la relación entre ambas entidades.
 *
 * Cada registro indica que un determinado rol
 * posee un permiso específico.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rol_permiso")
public class RolPermiso {
    /**
     * Clave primaria compuesta.
     */
    @EmbeddedId
    private RolPermisoId id_rol_permiso;

    /**
     * Rol asociado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idRol")
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    /**
     * Permiso asociado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idPermiso")
    @JoinColumn(name = "id_permiso", nullable = false)
    private Permiso permiso;

    /**
     * Fecha y hora en que el permiso fue asignado al rol.
     */
    @Column(name = "fecha_hora_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaHoraCreacion;
}
