package com.proveperu.m06_usuarios.entity;

import com.proveperu.m06_usuarios.enums.AccionPermiso;
import com.proveperu.m06_usuarios.enums.ModuloPermiso;
import com.proveperu.shared.entity.BaseCreationEntity;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un permiso dentro del sistema.
 *
 * Un permiso define una acción autorizada
 * sobre un módulo funcional específico.
 *
 * Ejemplos:
 *
 * VENTAS - CREAR
 * INVENTARIO - ELIMINAR
 * CLIENTES - LEER
 *
 * Hereda los atributos definidos en
 * {@link BaseCreationEntity}, incluyendo:
 *
 * <ul>
 *     <li>Estado lógico del registro.</li>
 *     <li>Fecha y hora de creación.</li>
 * </ul>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permiso")
public class Permiso extends BaseCreationEntity {
    /**
     * Identificador único del permiso.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Integer id;

    /**
     * Módulo funcional sobre el cual aplica el permiso.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "modulo", nullable = false, length = 30)
    private ModuloPermiso modulo;

    /**
     * Acción permitida dentro del módulo.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "accion", nullable = false, length = 15)
    private AccionPermiso accion;

    /**
     * Descripción funcional del permiso.
     */
    @Column(name = "descripcion", length = 200)
    private String descripcion;

    /**
     * Estado físico del permiso.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoActivoInactivo estadoFisico;

    /**
     * Roles que poseen este permiso.
     *
     * La relación es bidireccional y está administrada por
     * la entidad {@link RolPermiso} a través del atributo {@code permiso}.
     */
    @OneToMany(mappedBy = "permiso", fetch = FetchType.LAZY)
    @Builder.Default
    private List<RolPermiso> rolPermisos = new ArrayList<>();
}
