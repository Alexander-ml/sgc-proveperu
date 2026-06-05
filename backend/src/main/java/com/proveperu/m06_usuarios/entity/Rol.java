package com.proveperu.m06_usuarios.entity;

import com.proveperu.shared.entity.BaseAuditEntity;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un rol dentro del sistema.
 *
 * Un rol agrupa permisos y define las acciones que un usuario
 * puede realizar sobre los distintos módulos de la aplicación.
 *
 * Ejemplos de roles:
 * <ul>
 *     <li>ADMINISTRADOR</li>
 *     <li>CAJERO</li>
 *     <li>ALMACENERO</li>
 * </ul>
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
@Table(name = "rol")
public class Rol extends BaseAuditEntity {

    /**
     * Identificador único del rol.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    /**
     * Nombre único del rol.
     */
    @Column(name = "nombre_rol", nullable = false, unique = true, length = 50)
    private String nombreRol;

    /**
     * Descripción funcional del rol.
     */
    @Column(name = "descripcion", length = 200)
    private String descripcion;

    /**
     * Estado físico del rol.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoActivoInactivo estadoFisico;

    /**
     * Usuarios asociados al rol.
     *
     * La relación es bidireccional y está administrada por
     * la entidad {@link Usuario} a través del atributo {@code rol}.
     *
     * Se utiliza carga perezosa (LAZY) para evitar recuperar
     * innecesariamente todos los usuarios asociados al consultar un rol.
     */
    @OneToMany(mappedBy = "rol", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Usuario> usuarios = new ArrayList<>();

    /**
     * Permisos asociados al rol.
     *
     * La relación es bidireccional y está administrada por
     * la entidad {@link RolPermiso} mediante el atributo
     * {@code rol}.
     *
     * Cada registro de {@link RolPermiso} representa la
     * asignación de un permiso específico a este rol.
     */
    @OneToMany(mappedBy = "rol", fetch = FetchType.LAZY)
    @Builder.Default
    private List<RolPermiso> rolPermisos = new ArrayList<>();
}
