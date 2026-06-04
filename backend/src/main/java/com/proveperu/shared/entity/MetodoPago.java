package com.proveperu.shared.entity;

import com.proveperu.shared.enums.EstadoActivoInactivo;
import jakarta.persistence.*;
import lombok.*;

/**
 * Catálogo corporativo de métodos de pago disponibles
 * dentro del sistema.
 *
 * <p>
 * Esta entidad pertenece al Shared debido a que
 * es consumida por múltiples módulos de negocio.
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
@Table(name = "metodo_pago")
public class MetodoPago extends BaseAuditEntity{
    /**
     * Identificador único del método de pago.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private Integer idMetodoPago;

    /**
     * Nombre comercial del método de pago.
     */
    @Column(name = "nombre_metodo_pago", nullable = false, length = 50, unique = true)
    private String nombreMetodoPago; // Aplicar Patron - Strategy Pattern - aplicar un atributo codigo (pensarlo en grupo)

    /**
     * Descripción funcional del método de pago.
     */
    @Column(name = "descripcion", length = 100)
    private String descripcion;

    /**
     * Estado operativo del método de pago.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fisico", nullable = false, length = 20)
    private EstadoActivoInactivo estadoFisico;
}
