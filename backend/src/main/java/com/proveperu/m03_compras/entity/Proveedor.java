package com.proveperu.m03_compras.entity;

import com.proveperu.shared.entity.BaseAuditEntity;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import jakarta.persistence.*;
import lombok.*;

/**
 * Representa un proveedor registrado en el sistema.
 *
 * <p>
 * Los proveedores son las entidades encargadas del abastecimiento
 * de productos utilizados en los procesos de compra.
 * </p>
 *
 * <p>
 * Hereda de {@link BaseAuditEntity}, incorporando información
 * de auditoría como estado lógico, fecha de creación y fecha
 * de última actualización.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "proveedor")
public class Proveedor extends BaseAuditEntity {
    /**
     * Identificador único del proveedor.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor;

    /**
     * Número de RUC del proveedor.
     */
    @Column(name = "ruc", nullable = false, length = 11, unique = true)
    private String ruc;

    /**
     * Razón social registrada del proveedor.
     */
    @Column(name = "razon_social", nullable = false, length = 100)
    private String razonSocial;

    /**
     * Número telefónico de contacto.
     */
    @Column(name = "telefono", length = 20)
    private String telefono;

    /**
     * Dirección comercial del proveedor.
     */
    @Column(name = "direccion", length = 200)
    private String direccion;

    /**
     * Estado operativo del proveedor.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoActivoInactivo estadoFisico;
}
