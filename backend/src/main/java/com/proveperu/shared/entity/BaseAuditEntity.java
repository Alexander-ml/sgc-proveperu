package com.proveperu.shared.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Clase base que proporciona capacidades completas
 * de auditoría para las entidades del sistema.
 *
 * <p>
 * Extiende de {@link BaseCreationEntity}, heredando:
 * </p>
 *
 * <ul>
 *     <li>Estado lógico del registro.</li>
 *     <li>Fecha y hora de creación.</li>
 * </ul>
 *
 * <p>
 * Además incorpora la fecha y hora de última actualización,
 * permitiendo realizar trazabilidad sobre las modificaciones
 * efectuadas a la información almacenada.
 * </p>
 *
 * <p>
 * Debe utilizarse en entidades que requieran registrar
 * tanto la creación como las modificaciones posteriores
 * del registro.
 * </p>
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseAuditEntity extends BaseCreationEntity{

    /**
     * Fecha de última actualización.
     */
    @Column(name = "fecha_hora_actualizacion")
    private LocalDateTime fechaHoraActualizacion;
}
