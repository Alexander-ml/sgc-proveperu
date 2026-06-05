package com.proveperu.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de respuesta generado tras una autenticación exitosa.
 *
 * Contiene la información necesaria para establecer una sesión
 * autenticada del usuario, incluyendo el token JWT emitido,
 * datos básicos del usuario autenticado y metadatos asociados
 * al mecanismo de autorización.
 *
 * Este objeto forma parte del contrato de salida del módulo de
 * autenticación y es retornado por los endpoints encargados de
 * generación de credenciales de acceso.
 * */
@Getter
@Setter
@Builder
public class LoginResponse {
    /**
     * Token JWT emitido por el sistema tras validar correctamente
     * las credenciales del usuario. */
    private String token;
    /** * Tipo de esquema de autenticación utilizado para consumir
     * recursos protegidos de la API.
     * <p>
     *     Corresponde al valor {@code Bearer}.
     * </p> */
    private String tipo;
    /**
     * Identificador único utilizado por el usuario para acceder
     * a la plataforma. */
    private String usuarioLogin;
    /**
     * Nombre completo del usuario autenticado.*/
    private String nombreCompleto;
    /**
     * Rol principal asignado al usuario autenticado.*/
    private String rol;
    /**
     * Tiempo de vigencia del token expresado en segundos. */
    private long expiresIn;
}
