package com.proveperu.shared.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO genérico utilizado para estandarizar las respuestas exitosas y controladas
 * emitidas por la API.
 *
 * <p>
 * Este componente define un contrato uniforme de comunicación entre la capa
 * de presentación y los consumidores del sistema, permitiendo encapsular
 * información de resultado, mensajes funcionales y datos de negocio dentro
 * de una estructura consistente.
 * </p>
 *
 * <p>
 * Es utilizado por los controladores para construir respuestas homogéneas
 * independientemente del módulo funcional que procese la solicitud.
 * </p>
 *
 * @param <T> tipo de dato transportado dentro de la respuesta.
 */
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Los campos que tengan valor null NO se incluirán en el JSON de respuesta.
public class ApiResponse<T> {
    /**
     * Indica si la operación fue ejecutada satisfactoriamente.
     */
    private boolean success;
    /**
     * Mensaje funcional asociado al resultado de la operación.
     */
    private String message;
    /**
     * Información de negocio devuelta por la operación ejecutada.
     */
    private T data;
    /**
     * Fecha y hora en la que fue construida la respuesta.
     */
    private LocalDateTime timestamp;

    private ApiResponse(boolean success, String message, T data) {
        this.success  = success;
        this.message  = message;
        this.data     = data;
        this.timestamp = LocalDateTime.now();
    }

    // ----------------------------------------------------------------
    // Métodos de fábrica — respuestas exitosas
    // ----------------------------------------------------------------

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operación exitosa", data);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> successNoContent(String message) {
        return new ApiResponse<>(true, message, null);
    }

    // ----------------------------------------------------------------
    // Métodos de fábrica — respuestas con error
    // ----------------------------------------------------------------

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}
