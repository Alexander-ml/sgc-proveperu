package com.proveperu.shared.enums;

/**
 * Representa el estado lógico de un registro dentro del sistema.
 *
 * Se almacena en base de datos mediante valores numéricos:
 * 0 = INACTIVO
 * 1 = ACTIVO
 */
public enum EstadoLogico {
    INACTIVO(0),
    ACTIVO(1);

    private final Integer valor;

    EstadoLogico(Integer valor) {
        this.valor = valor;
    }

    public Integer getValor() {
        return valor;
    }

    public static EstadoLogico fromValor(Integer valor) {

        for (EstadoLogico estado : values()) {

            if (estado.valor.equals(valor)) {
                return estado;
            }

        }

        throw new IllegalArgumentException(
                "Estado lógico no válido: " + valor
        );
    }
}
