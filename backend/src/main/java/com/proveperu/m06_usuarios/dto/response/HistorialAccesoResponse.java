package com.proveperu.m06_usuarios.dto.response;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Respuesta utilizada para mostrar un registro
 * del historial de accesos de los usuarios.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialAccesoResponse {
    
    /**
     * Identificador de la sesión registrada.
     */
    private Integer idSesion;

    /**
     * Fecha y hora en que ocurrió el acceso.
     */
    private LocalDateTime fechaHora;

    /**
     * Nombre completo del usuario.
     */
    private String usuario;

    /**
     * Correo utilizado para iniciar sesión.
     */
    private String usuarioLogin;

    /**
     * Acción realizada por el usuario.
     */
    private String accionRegistrada;
}
