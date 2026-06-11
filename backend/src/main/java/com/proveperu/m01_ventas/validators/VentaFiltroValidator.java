package com.proveperu.m01_ventas.validators;

import com.proveperu.m01_ventas.dto.request.VentaFiltroRequest;
import com.proveperu.shared.exception.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

@Component
public class VentaFiltroValidator {

    private static final Set<String> DIRECCIONES_VALIDAS =
            Set.of("ASC", "DESC");

    public void validar(VentaFiltroRequest filtro) {

        validarRangoFechas(filtro);
        validarPaginacion(filtro);
        validarDireccionOrdenamiento(filtro);
    }

    private void validarRangoFechas(VentaFiltroRequest filtro) {

        if (filtro.getFechaInicio() != null
                && filtro.getFechaFin() != null
                && filtro.getFechaInicio().isAfter(filtro.getFechaFin())) {

            throw new ValidationException(
                    "La fecha de inicio no puede ser posterior a la fecha fin.");
        }
    }

    private void validarPaginacion(VentaFiltroRequest filtro) {

        if (filtro.getPage() < 0) {
            throw new ValidationException(
                    "El número de página no puede ser negativo.");
        }

        if (filtro.getSize() <= 0) {
            throw new ValidationException(
                    "El tamaño de página debe ser mayor a cero.");
        }

        if (filtro.getSize() > 100) {
            throw new ValidationException(
                    "El tamaño de página no puede superar 100 elementos.");
        }
    }

    private void validarDireccionOrdenamiento(VentaFiltroRequest filtro) {

        if (!StringUtils.hasText(filtro.getDirection())) {
            filtro.setDirection("DESC");
            return;
        }

        String direction = filtro.getDirection().trim().toUpperCase();

        if (!DIRECCIONES_VALIDAS.contains(direction)) {
            throw new ValidationException(
                    "La dirección de ordenamiento debe ser ASC o DESC.");
        }

        filtro.setDirection(direction);
    }
}
