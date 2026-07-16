package com.proveperu.m05_gestion_clientes.entity;

import com.proveperu.m05_gestion_clientes.enums.TipoCliente;
import com.proveperu.shared.entity.BaseAuditEntity;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un cliente del sistema.
 *
 * Puede ser una persona natural o una empresa, manejando
 * información fiscal y de contacto.
 *
 * Hereda de {@link BaseAuditEntity}, incorporando información
 * de auditoría como estado lógico, fecha de creación y fecha
 * de última actualización.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cliente")
public class Cliente extends BaseAuditEntity {
    /**
     * Identificador único del cliente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    /**
     * Tipo de cliente:
     * - PERSONA: cliente individual
     * - EMPRESA: cliente corporativo
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente", nullable = false, length = 10)
    private TipoCliente tipoCliente;

    /**
     * Nombre completo del cliente (personas naturales).
     */
    @Column(name = "nombre_completo", length = 100)
    private String nombreCompleto;

    /**
     * Razón social de la empresa (clientes jurídicos).
     */
    @Column(name = "razon_social", length = 150)
    private String razonSocial;

    /**
     * Documento nacional de identidad (solo PERSONA).
     */
    @Column(name = "dni", length = 8)
    private String dni;

    /**
     * Registro único de contribuyente (solo EMPRESA).
     */
    @Column(name = "ruc", length = 11)
    private String ruc;

    /**
     * Número telefónico de contacto.
     */
    @Column(name = "telefono", length = 20)
    private String telefono;

    /**
     * Dirección física del cliente.
     */
    @Column(name = "direccion", length = 200)
    private String direccion;

    /**
     * Estado físico del cliente.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoActivoInactivo estadoCliente;
}
