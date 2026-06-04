package com.proveperu.m06_usuarios.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * Clave primaria compuesta de la entidad RolPermiso.
 *
 * Representa la asociación única entre un rol
 * y un permiso dentro del sistema.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class RolPermisoId implements Serializable {
    /**
     * Identificador del rol.
     */
    @Column(name = "id_rol")
    private Integer idRol;

    /**
     * Identificador del permiso.
     */
    @Column(name = "id_permiso")
    private Integer idPermiso;
}
