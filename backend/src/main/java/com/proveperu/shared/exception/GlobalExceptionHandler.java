package com.proveperu.shared.exception;

import com.proveperu.shared.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * Manejador global de excepciones de la aplicación.
 *
 * <p>
 * Centraliza la captura, transformación y estandarización de excepciones
 * producidas durante el procesamiento de solicitudes HTTP, garantizando
 * respuestas consistentes para todos los consumidores de la API.
 * </p>
 *
 * <p>
 * Este componente actúa como una capa transversal de manejo de errores,
 * encargándose de traducir excepciones técnicas, funcionales y de seguridad
 * en respuestas HTTP estructuradas mediante {@link ErrorResponse}.
 * </p>
 *
 * <p>
 * Además de generar respuestas uniformes, registra eventos relevantes
 * utilizando SLF4J para facilitar la trazabilidad, monitoreo y diagnóstico
 * de incidencias dentro de la aplicación.
 * </p>
 *
 * <p>
 * La estrategia de logging utilizada es la siguiente:
 * </p>
 * <ul>
 *     <li><b>WARN</b>: errores esperados de negocio, validación o seguridad.</li>
 *     <li><b>ERROR</b>: fallos inesperados o errores internos del sistema.</li>
 * </ul>
 *
 * <p>
 * Sus principales responsabilidades incluyen:
 * </p>
 * <ul>
 *     <li>Gestionar errores de validación de datos.</li>
 *     <li>Procesar excepciones de autenticación y autorización.</li>
 *     <li>Transformar excepciones de negocio en respuestas HTTP apropiadas.</li>
 *     <li>Evitar la exposición de información sensible.</li>
 *     <li>Garantizar uniformidad en el formato de errores de la API.</li>
 *     <li>Registrar eventos relevantes para auditoría y diagnóstico.</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestiona errores de validación funcional producidos por reglas
     * de negocio de la aplicación.
     *
     * <p>
     * A diferencia de las validaciones realizadas mediante Bean Validation,
     * esta excepción representa incumplimientos detectados durante la ejecución
     * de la lógica de negocio, tales como inconsistencias de datos,
     * restricciones operativas o reglas propias del dominio.
     * </p>
     *
     * <p>
     * El evento es registrado con nivel {@code WARN}, ya que representa
     * una situación esperada derivada de datos o acciones inválidas
     * realizadas por el consumidor de la API.
     * </p>
     *
     * @param ex excepción de validación funcional.
     * @return respuesta HTTP 400 (Bad Request) con el detalle del error.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        log.warn(
                "Error de validación de DTO. Campos inválidos: {}",
                fieldErrors
        );

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Validation Failed")
                        .message("Los datos enviados no son válidos")
                        .timestamp(LocalDateTime.now())
                        .fieldErrors(fieldErrors)
                        .build()
        );
    }

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
     * <p>
     * Los errores detectados son registrados con nivel {@code WARN}
     * para facilitar el análisis de solicitudes inválidas sin afectar
     * el flujo normal de ejecución de la aplicación.
     * </p>
     *
     * @param ex excepción generada durante el proceso de validación.
     * @return respuesta HTTP 400 con el detalle de los errores detectados.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {

        log.warn(
                "Validación de negocio fallida: {}",
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Validation Error")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    /**
     * Gestiona errores producidos durante procesos de autenticación cuando
     * las credenciales proporcionadas por el usuario no son válidas.
     *
     * <p>
     * Este escenario ocurre típicamente cuando el nombre de usuario,
     * correo electrónico o contraseña suministrados no coinciden con
     * los registros almacenados por el sistema.
     * </p>
     *
     * <p>
     * El intento fallido es registrado con nivel {@code WARN}
     * debido a que representa una situación esperada dentro
     * del flujo de autenticación.
     * </p>
     *
     * @param ex excepción generada por Spring Security durante la autenticación.
     * @return respuesta HTTP 401 (Unauthorized).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {

        log.warn(
                "Intento de autenticación fallido: {}",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
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
     * <p>
     * Este escenario ocurre cuando un usuario autenticado intenta ejecutar
     * una operación o acceder a un recurso protegido sin contar con los
     * privilegios necesarios según las reglas de autorización definidas
     * por la aplicación.
     * </p>
     *
     * <p>
     * El evento es registrado con nivel {@code WARN} para permitir
     * el seguimiento de intentos de acceso no autorizados.
     * </p>
     *
     * @param ex excepción asociada a la denegación de acceso.
     * @return respuesta HTTP 403 (Forbidden).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {

        log.warn("Acceso denegado: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .error("Forbidden")
                        .message("No tienes permiso para acceder a este recurso")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    /**
     * Gestiona excepciones relacionadas con recursos inexistentes
     * dentro del dominio de negocio.
     *
     * <p>
     * Se utiliza para informar que una entidad solicitada no pudo ser
     * localizada en el sistema manteniendo una respuesta coherente y
     * alineada con los principios REST.
     * </p>
     *
     * <p>
     * El evento es registrado con nivel {@code WARN} incluyendo información
     * sobre el recurso solicitado y el identificador utilizado durante
     * la búsqueda.
     * </p>
     *
     * @param ex excepción que contiene la descripción del recurso no encontrado.
     * @return respuesta HTTP 404 (Not Found).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {

        log.warn(
                "Recurso no encontrado. Recurso={}, Identificador={}",
                ex.getRecurso(),
                ex.getIdentificador()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error("Not Found")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    /**
     * Captura cualquier excepción no gestionada explícitamente por la aplicación.
     *
     * <p>
     * Este manejador constituye la última línea de defensa ante errores
     * inesperados, evitando la exposición de información sensible y
     * garantizando que el cliente reciba una respuesta controlada.
     * </p>
     *
     * <p>
     * Debido a que este escenario representa un fallo no previsto,
     * la excepción completa es registrada con nivel {@code ERROR},
     * incluyendo su stack trace para facilitar el diagnóstico y
     * resolución del problema.
     * </p>
     *
     * @param ex excepción no gestionada producida durante la ejecución.
     * @return respuesta HTTP 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(
            Exception ex) {

        log.error("Error interno no controlado", ex);

        return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR
        ).body(
                ErrorResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error("Internal Server Error")
                        .message("Ocurrió un error interno inesperado")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
