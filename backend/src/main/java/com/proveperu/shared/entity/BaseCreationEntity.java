package com.proveperu.shared.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Clase base que incorpora información de creación
 * sobre las entidades del sistema.
 *
 * <p>
 * Extiende de {@link BaseEstadoLogicoEntity}, heredando
 * el manejo del estado lógico y agregando la fecha y hora
 * de creación del registro.
 * </p>
 *
 * <p>
 * Debe utilizarse en entidades que requieran conocer
 * cuándo fueron registradas, pero que no necesiten
 * almacenar información de actualización.
 * </p>
 */
@MappedSuperclass
@Getter
@Setter
public abstract  class BaseCreationEntity extends  BaseEstadoLogicoEntity{

    /**
     * Fecha de creación del registro.
     */
    @Column(name = "fecha_hora_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaHoraCreacion;
}
