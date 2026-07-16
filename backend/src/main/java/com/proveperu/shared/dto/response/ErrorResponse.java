package com.proveperu.shared.dto.response;

import com.proveperu.shared.exception.GlobalExceptionHandler;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO utilizado para representar errores procesados por la aplicación
 * y enviados a los consumidores de la API.
 *
 * <p>
 * Este componente proporciona una estructura uniforme para la comunicación
 * de errores técnicos, funcionales y de validación, permitiendo que los
 * clientes reciban información consistente sobre las causas que impidieron
 * completar una solicitud.
 * </p>
 *
 * <p>
 *     Es utilizado por {@link GlobalExceptionHandler}, validadores y componentes de seguridad.
 * </p>
 *
 * <p>
 * Además de la información general del error, soporta el envío de errores
 * asociados a campos específicos, facilitando la validación de formularios
 * y solicitudes complejas.
 * </p>
 */
@Getter
@Builder
public class ErrorResponse {
    private int status; // Código HTTP
    private String error; // Nombre del error
    private String message; // Descripción del error
    private LocalDateTime timestamp; // Fecha y hora del error
    private Map<String, String> fieldErrors; // Errores por campo
}
