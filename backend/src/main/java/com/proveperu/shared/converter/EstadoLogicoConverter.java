package com.proveperu.shared.converter;

import com.proveperu.shared.enums.EstadoLogico;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Conversor JPA encargado de transformar el enum
 * {@link EstadoLogico} a su representación numérica
 * en base de datos y viceversa.
 *
 * Permite trabajar con enums en Java mientras
 * PostgreSQL almacena valores enteros.
 * ACTIVO = 1
 * INACTIVO = 0
 */
@Converter(autoApply = false)
public class EstadoLogicoConverter  implements AttributeConverter<EstadoLogico, Integer> {

    @Override
    public Integer convertToDatabaseColumn(
            EstadoLogico estadoLogico) {

        if (estadoLogico == null) {
            return null;
        }

        return estadoLogico.getValor();
    }

    @Override
    public EstadoLogico convertToEntityAttribute(
            Integer valor) {

        if (valor == null) {
            return null;
        }

        return EstadoLogico.fromValor(valor);
    }
}
