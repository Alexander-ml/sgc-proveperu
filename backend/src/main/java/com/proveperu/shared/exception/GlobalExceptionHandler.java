package com.proveperu.shared.exception;

import com.proveperu.shared.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * Manejador global de excepciones de la aplicación.
 *
 * <p>
 * Este componente centraliza la captura, transformación y estandarización
 * de errores producidos durante el procesamiento de solicitudes HTTP,
 * garantizando respuestas consistentes para todos los consumidores de la API.
 * </p>
 *
 * <p>
 *     Encargado de traducir excepciones técnicas y funcionales en respuestas HTTP
 *     estructuradas mediante {@link ErrorResponse}.
 * </p>
 *
 * <p>
 *     Sus principales responsabilidades incluyen:
 * </p>
 * <ul>
 *     <li>Gestionar errores de validación de datos.</li>
 *     <li>Procesar excepciones de autenticación y autorización.</li>
 *     <li>Transformar excepciones de negocio en respuestas HTTP apropiadas.</li>
 *     <li>Evitar la exposición de detalles internos de la aplicación.</li>
 *     <li>Garantizar uniformidad en el formato de errores de la API.</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Procesa errores generados por validaciones de DTOs utilizando
     * Bean Validation y la anotación {@code @Valid}.
     *
     * <p>
     * Recopila los errores asociados a cada campo inválido y construye
     * una respuesta estructurada que permite a los consumidores identificar
     * con precisión los datos que incumplen las reglas de validación.
     * </p>
     *
     * @param ex excepción generada durante el proceso de validación.
     * @return respuesta HTTP 400 con el detalle de los errores detectados.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .status(400)
                        .error("Validation Failed")
                        .message("Los datos enviados no son válidos")
                        .timestamp(LocalDateTime.now())
                        .fieldErrors(fieldErrors)
                        .build()
        );
    }

    /**
     * Gestiona errores producidos durante procesos de autenticación cuando
     * las credenciales proporcionadas por el usuario no son válidas.
     *
     * @param ex excepción generada por Spring Security durante la autenticación.
     * @return respuesta HTTP 401 indicando fallo de autenticación.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .status(401)
                        .error("Unauthorized")
                        .message("Usuario o contraseña incorrectos")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    /**
     * Gestiona intentos de acceso a recursos para los cuales el usuario
     * autenticado no posee los permisos requeridos.
     *
     * @param ex excepción asociada a la denegación de acceso.
     * @return respuesta HTTP 403 indicando falta de permisos.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse.builder()
                        .status(403)
                        .error("Forbidden")
                        .message("No tienes permiso para acceder a este recurso")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    /**
     * Gestiona excepciones relacionadas con recursos inexistentes dentro
     * del dominio de negocio.
     *
     * <p>
     * Se utiliza para informar que una entidad solicitada no pudo ser
     * localizada en el sistema manteniendo una respuesta coherente y
     * alineada con los estándares REST.
     * </p>
     *
     * @param ex excepción que contiene la descripción del recurso no encontrado.
     * @return respuesta HTTP 404 con el detalle del error.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.builder()
                        .status(404)
                        .error("Not Found")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    /**
     * Captura cualquier excepción no controlada explícitamente por la aplicación.
     *
     * <p>
     * Este manejador constituye la última línea de defensa ante errores
     * inesperados, evitando la exposición de información sensible y
     * garantizando que el cliente reciba una respuesta controlada.
     * </p>
     *
     * @param ex excepción no gestionada producida durante la ejecución.
     * @return respuesta HTTP 500 indicando un error interno del servidor.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .status(500)
                        .error("Internal Server Error")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
