package com.proveperu.shared.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

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
@Builder
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

    /**
     * Construye una respuesta exitosa utilizando el formato estándar de la API.
     *
     * <p>
     * Este método centraliza la creación de respuestas satisfactorias,
     * incorporando automáticamente la marca temporal de generación.
     * </p>
     *
     * @param mensaje mensaje descriptivo asociado al resultado de la operación.
     * @param data información de negocio que será retornada al consumidor.
     * @param <T> tipo de dato contenido en la respuesta.
     * @return instancia de {@link ApiResponse} configurada como respuesta exitosa.
     */
    public static <T> ApiResponse<T> ok(String mensaje, T data){
        return ApiResponse.<T>builder()
                .success(true)
                .message(mensaje)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Construye una respuesta de error controlado utilizando el formato
     * estándar de la API.
     *
     * <p>
     * Este método permite informar fallos funcionales o de negocio
     * manteniendo una estructura de respuesta consistente para los clientes.
     * </p>
     *
     * @param message descripción del error ocurrido.
     * @param <T> tipo genérico de la respuesta.
     * @return instancia de {@link ApiResponse} configurada como respuesta fallida.
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
