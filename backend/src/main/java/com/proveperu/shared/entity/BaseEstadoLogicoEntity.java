package com.proveperu.shared.entity;

import com.proveperu.shared.converter.EstadoLogicoConverter;
import com.proveperu.shared.enums.EstadoLogico;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase base que proporciona el manejo del estado lógico
 * de las entidades del sistema.
 *
 * <p>
 * Permite implementar el borrado lógico (Soft Delete)
 * y controlar la disponibilidad de los registros sin
 * necesidad de eliminarlos físicamente de la base de datos.
 * </p>
 *
 * <p>
 * Todas las entidades que posean únicamente el atributo
 * {@code estado_logico} deben extender esta clase.
 * </p>
 *
 * <p>
 * El estado lógico es persistido mediante el
 * {@link EstadoLogicoConverter}, permitiendo trabajar
 * con el enum {@link EstadoLogico} mientras la base de datos
 * almacena valores numéricos.
 * </p>
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEstadoLogicoEntity {

    /**
     * Estado lógico del registro.
     *
     * <ul>
     *     <li>{@link EstadoLogico#ACTIVO}: registro disponible.</li>
     *     <li>{@link EstadoLogico#INACTIVO}: registro deshabilitado.</li>
     * </ul>
     */
    @Convert(converter = EstadoLogicoConverter.class)
    @Column(name = "estado_logico", nullable = false)
    private EstadoLogico estadoLogico;
}
