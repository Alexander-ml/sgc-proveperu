package com.proveperu.shared.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Los campos que tengan valor null NO se incluirán en el JSON de respuesta.
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    // PARA RESPUESTAS EXITOSAS
    public static <T> ApiResponse<T> ok(String mensaje, T data){
        return ApiResponse.<T>builder()
                .success(true) // indica que la operación fue exitosa
                .message(mensaje) // mensaje cliente
                .data(data) // contenido de la respuesta
                .timestamp(LocalDateTime.now()) // momento de la respuesta
                .build();
    }

    // PARA RESPUESTAS FALLIDAS
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false) // indica que la operación no fue exitosa
                .message(message) // mensaje cliente
                .timestamp(LocalDateTime.now()) // momento de la respuesta
                .build();
    }
}
