package com.proveperu.auth.controller;

import com.proveperu.auth.dto.request.LoginRequest;
import com.proveperu.auth.dto.response.LoginResponse;
import com.proveperu.auth.service.AuthService;
import com.proveperu.shared.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST encargado de gestionar los procesos de autenticación
 * de usuarios dentro del sistema.
 *
 * <p>
 * Expone los endpoints públicos relacionados con el acceso a la plataforma,
 * permitiendo validar credenciales y generar tokens JWT para el consumo
 * de recursos protegidos.
 * </p>
 *
 * <p>
 * Todos los endpoints definidos en este controlador pertenecen al contexto
 * de autenticación y no requieren un token previo para su ejecución.
 * </p>
 *
 * @author Alexander
 */
@Tag(
        name = "Autenticación",
        description = "Gestión de autenticación y generación de tokens JWT"
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * Servicio encargado de ejecutar la lógica de autenticación
     * y generación de tokens de acceso.
     */
    private final AuthService authService;

    /**
     * Autentica las credenciales de un usuario y genera un token JWT
     * válido para acceder a los recursos protegidos del sistema.
     *
     * <p>
     * El endpoint valida la información recibida, verifica la identidad
     * del usuario y retorna un token de acceso junto con los datos
     * necesarios para la sesión autenticada.
     * </p>
     *
     * @param request información de autenticación enviada por el cliente,
     *                incluyendo las credenciales requeridas para iniciar sesión
     * @return respuesta HTTP 200 con el token JWT y la información
     *         correspondiente al usuario autenticado
     */
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica las credenciales del usuario y genera un token JWT."
    )
    @PostMapping("/login")
    public ResponseEntity<  ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.ok("Autenticación exitosa", response)
        );
    }
}
