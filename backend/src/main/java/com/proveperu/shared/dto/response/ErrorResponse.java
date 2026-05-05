package com.proveperu.shared.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {
    private int status; // Código HTTP (ejm: 400, 401, 403)
    private String error; // Nombre del error (ejm: BAD_REQUEST)
    private String message; // Descripción del error
    private LocalDateTime timestamp; // Fecha y hora del error
    private Map<String, String> fieldErrors; // Errores por campo (validaciones)
}
