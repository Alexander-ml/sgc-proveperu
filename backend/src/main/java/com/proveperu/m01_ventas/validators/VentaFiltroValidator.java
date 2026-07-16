package com.proveperu.m01_ventas.validators;

import com.proveperu.m01_ventas.dto.request.VentaFiltroRequest;
import com.proveperu.shared.exception.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * Validador funcional del filtro de ventas.
 *
 * <p>
 * Esta clase aplica reglas de negocio y normalización sobre los criterios
 * de búsqueda utilizados por el endpoint de listado de ventas.
 * </p>
 *
 * <p>
 * Su responsabilidad es complementar la validación declarativa del DTO,
 * asegurando consistencia en el rango de fechas, campo de ordenamiento de sort y los
 * parámetros de ordenamiento dirección antes de construir la consulta dinámica.
 * </p>
 */
@Component
public class VentaFiltroValidator {

    private static final int MIN_PAGE = 0;
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 100;

    /**
     * Direcciones de ordenamiento permitidas por el sistema.
     */
    private static final Set<String> DIRECCIONES_VALIDAS = Set.of("ASC", "DESC");

    /**
     * Campos de ordenamiento permitidos para el listado de ventas.
     *
     * <p>
     * Deben corresponder a propiedades válidas de la entidad o a los campos
     * soportados por la capa de consulta.
     * </p>
     */
    private static final Set<String> CAMPOS_ORDENAMIENTO_VALIDOS = Set.of(
            "idVenta",
            "fechaHoraVenta",
            "total"
    );


    /**
     * Valida y normaliza el filtro de ventas recibido desde la capa web.
     *
     * <p>
     * Verifica el rango de fechas, la paginación, el campo de ordenamiento
     * y la dirección de ordenamiento. En caso de encontrar una regla inválida,
     * lanza una {@link ValidationException}.
     * </p>
     *
     * @param filtro criterios de búsqueda y paginación.
     */
    public void validar(VentaFiltroRequest filtro) {
        if (filtro == null) {
            throw new ValidationException("El filtro de ventas no puede ser nulo.");
        }

        validarRangoFechas(filtro);
        validarPaginacion(filtro);
        validarCampoOrdenamiento(filtro);
        validarDireccionOrdenamiento(filtro);
    }

    /**
     * Valida que la fecha inicial no sea posterior a la fecha final.
     *
     * @param filtro filtro de búsqueda a validar.
     */
    private void validarRangoFechas(VentaFiltroRequest filtro) {
        if (filtro.getFechaInicio() != null
                && filtro.getFechaFin() != null
                && filtro.getFechaInicio().isAfter(filtro.getFechaFin())) {

            throw new ValidationException(
                    "La fecha de inicio no puede ser posterior a la fecha fin."
            );
        }
    }

    /**
     * Valida que los parámetros de paginación sean compatibles con PageRequest.
     */
    private void validarPaginacion(VentaFiltroRequest filtro) {
        if (filtro.getPage() < MIN_PAGE) {
            throw new ValidationException("La página no puede ser negativa.");
        }

        if (filtro.getSize() < MIN_SIZE || filtro.getSize() > MAX_SIZE) {
            throw new ValidationException("El tamaño de página debe estar entre 1 y 100.");
        }
    }

    /**
     * Valida que el campo de ordenamiento exista dentro de los campos permitidos.
     *
     * @param filtro filtro de búsqueda a validar.
     */
    private void validarCampoOrdenamiento(VentaFiltroRequest filtro) {
        if (!StringUtils.hasText(filtro.getSort())) {
            filtro.setSort("fechaHoraVenta");
            return;
        }

        String sort = filtro.getSort().trim();

        if (!CAMPOS_ORDENAMIENTO_VALIDOS.contains(sort)) {
            throw new ValidationException("El campo de ordenamiento no es válido.");
        }

        filtro.setSort(sort);
    }

    /**
     * Valida y normaliza la dirección de ordenamiento.
     *
     * <p>
     * Si la dirección no viene informada, se asigna por defecto {@code DESC}.
     * </p>
     *
     * @param filtro filtro de búsqueda a validar.
     */
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
