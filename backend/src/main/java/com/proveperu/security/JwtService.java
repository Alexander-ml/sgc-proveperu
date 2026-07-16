package com.proveperu.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio responsable de la generación, validación y extracción de información
 * contenida en los tokens JWT utilizados por el sistema.
 *
 * <p>
 * Este componente centraliza toda la lógica relacionada con la seguridad basada
 * en JSON Web Tokens (JWT), permitiendo emitir credenciales de acceso firmadas,
 * validar su integridad criptográfica y recuperar la información necesaria para
 * establecer el contexto de autenticación de los usuarios.
 * </p>
 *
 * <p>
 * Dentro de la arquitectura del proyecto, pertenece a la capa
 * <strong>Security</strong> y actúa como servicio de infraestructura utilizado
 * por los procesos de autenticación, autorización y validación de solicitudes
 * protegidas.
 * </p>
 *
 * <p>
 * Los tokens generados incorporan información de identidad y autorización,
 * permitiendo implementar un modelo de autenticación stateless sin necesidad
 * de mantener sesiones activas en el servidor.
 * </p>
 */
@Slf4j
@Service
public class JwtService {

    /**
     * Clave secreta utilizada para la firma y validación criptográfica de los
     * tokens JWT emitidos por la aplicación.
     */
    @Value("${jwt.secret-key}")
    private String secretKey;
    /**
     * Este valor determina la fecha de expiración incorporada en cada token
     * generado por el sistema.
     */
    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    /**
     * Genera un nuevo token JWT para el usuario autenticado.
     *
     * <p>
     * El token incorpora información de identidad y autorización, incluyendo
     * el nombre de usuario y el rol asignado, además de las fechas de emisión
     * y expiración requeridas para su validación posterior.
     * </p>
     *
     * @param userDetails información de seguridad del usuario autenticado.
     * @param rol nombre del rol asignado al usuario.
     * @return token JWT firmado digitalmente y listo para ser utilizado
     *         en solicitudes autenticadas.
     */
    public String generateToken(UserDetails userDetails, String rol) {
         log.info(
            "Generando JWT para usuario {} con rol {}",
            userDetails.getUsername(),
            rol
    );

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("rol", rol);
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername()) // DB: usuario_login, Entidad: usuarioLogin
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrae el identificador principal del usuario contenido en el token.
     *
     * <p>
     * Este valor corresponde al subject registrado durante la generación
     * del token y representa la identidad autenticada.
     * </p>
     *
     * @param token token JWT del cual se extraerá la información.
     * @return nombre de usuario asociado al token.
     */
    public String extractUsername(String token) {
         log.info("Extrayendo username desde JWT");

        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Obtiene el rol de autorización almacenado dentro del token JWT.
     *
     * @param token token JWT que contiene la información de autorización.
     * @return nombre del rol asignado al usuario autenticado.
     */
    public String extractRol(String token) {
        return extractClaim(token, claims -> claims.get("rol", String.class));
    }

    /**
     * Verifica que un token JWT sea válido para un usuario determinado.
     *
     * <p>
     * La validación contempla:
     * </p>
     * <ul>
     *     <li>Correspondencia entre el usuario del token y el usuario autenticado.</li>
     *     <li>Verificación de vigencia del token.</li>
     * </ul>
     *
     * @param token token JWT a validar.
     * @param userDetails información de seguridad del usuario esperado.
     * @return {@code true} si el token es válido para el usuario indicado;
     *         {@code false} en caso contrario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
          log.info(
            "Validando token para usuario {}",
            userDetails.getUsername()
    );
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Determina si el token ha superado su fecha de expiración.
     *
     * @param token token JWT a evaluar.
     * @return {@code true} si el token se encuentra expirado;
     *         {@code false} si aún es válido temporalmente.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Recupera la fecha de expiración registrada dentro del token.
     *
     * @param token token JWT a analizar.
     * @return fecha de expiración asociada al token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim específico del token utilizando una función de resolución.
     *
     * <p>
     * Este método centraliza la obtención de información almacenada en los
     * claims del token y permite reutilizar la lógica de extracción para
     * distintos tipos de datos.
     * </p>
     *
     * @param token token JWT que contiene los claims.
     * @param claimsResolver función encargada de resolver el dato requerido.
     * @param <T> tipo de dato esperado.
     * @return valor obtenido desde los claims del token.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Obtiene la totalidad de los claims contenidos en un token JWT.
     *
     * <p>
     * Durante el proceso se valida la firma digital del token para garantizar
     * su integridad y autenticidad antes de exponer la información contenida.
     * </p>
     *
     * @param token token JWT a procesar.
     * @return conjunto completo de claims presentes en el token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                // Verifica la firma con la clave secreta
                .verifyWith(getSigningKey())
                .build()
                // Decodifica el token
                .parseSignedClaims(token)
                // Obtiene el payload (Claims)
                .getPayload();
    }

    /**
     * Convierte la clave secreta configurada en una instancia criptográfica
     * utilizable por la librería JWT para firmar y verificar tokens.
     *
     * @return clave criptográfica derivada de la configuración del sistema.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Obtiene el tiempo de vigencia configurado para los tokens JWT.
     *
     * @return duración de los tokens expresada en milisegundos.
     */
    public long getExpirationMs() {
        return expirationMs;
    }
}
