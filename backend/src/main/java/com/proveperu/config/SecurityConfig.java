package com.proveperu.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.proveperu.security.JwtAuthenticationFilter;
import com.proveperu.security.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

/**
 * Configuración central de seguridad de la aplicación.
 *
 * <p>
 * Esta clase define la política global de seguridad utilizada por Spring Security,
 * incluyendo los mecanismos de autenticación, autorización, procesamiento de tokens
 * JWT, configuración CORS y estrategia de gestión de sesiones.
 * </p>
 *
 * <p>
 * Dentro de la arquitectura del sistema, este componente pertenece a la capa
 * de infraestructura de seguridad y actúa como punto de integración entre
 * Spring Security, los servicios de autenticación y los filtros personalizados
 * responsables de validar credenciales y tokens de acceso.
 * </p>
 *
 * <p>
 * La configuración implementa un modelo de autenticación stateless basado en JWT,
 * eliminando la necesidad de mantener sesiones HTTP en el servidor y permitiendo
 * una arquitectura desacoplada y escalable para clientes web y aplicaciones
 * externas.
 * </p>
 */
@Configuration
@EnableWebSecurity // Activa seguridad web
@EnableMethodSecurity // habilita @PreAuthorize en los controladores
@RequiredArgsConstructor
public class SecurityConfig {
    /**
     * Filtro responsable de interceptar las solicitudes entrantes y validar
     * los tokens JWT presentes en la cabecera de autorización.
     */
    private final JwtAuthenticationFilter jwtAuthFilter;
    /**
     * Servicio encargado de recuperar la información de autenticación y autorización
     * de los usuarios desde la fuente de datos configurada.
     */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Construye y configura la cadena principal de filtros de seguridad
     * utilizada por Spring Security para procesar las solicitudes HTTP.
     *
     * <p>
     * La configuración establece:
     * </p>
     * <ul>
     *     <li>Desactivación de protección CSRF para autenticación basada en JWT.</li>
     *     <li>Configuración de políticas CORS.</li>
     *     <li>Definición de endpoints públicos y protegidos.</li>
     *     <li>Gestión de sesiones sin estado.</li>
     *     <li>Integración del proveedor de autenticación.</li>
     *     <li>Registro del filtro JWT dentro de la cadena de seguridad.</li>
     * </ul>
     *
     * @param http objeto de configuración de seguridad HTTP proporcionado por Spring Security.
     * @return cadena de filtros de seguridad configurada para la aplicación.
     * @throws Exception si ocurre algún error durante la construcción de la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/actuator/health",
                                "/actuator/info",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    /**
     * Configura el proveedor de autenticación encargado de validar credenciales
     * mediante la integración entre Spring Security, la fuente de datos de usuarios
     * y el mecanismo de cifrado de contraseñas.
     *
     * <p>
     * Este proveedor utiliza la implementación personalizada de
     * {@code UserDetailsService} para recuperar usuarios y un
     * {@code PasswordEncoder} para validar contraseñas cifradas.
     * </p>
     *
     * @return proveedor de autenticación utilizado por el sistema.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Expone el {@link AuthenticationManager} de Spring Security para su uso
     * dentro de los servicios de autenticación de la aplicación.
     *
     * <p>
     * Este componente actúa como coordinador central del proceso de autenticación,
     * delegando la validación de credenciales a los proveedores configurados.
     * </p>
     *
     * @param config configuración de autenticación proporcionada por Spring Security.
     * @return instancia configurada del administrador de autenticación.
     * @throws Exception si ocurre un error durante la obtención del administrador.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define el mecanismo de codificación de contraseñas utilizado por la aplicación.
     *
     * <p>
     * Se emplea el algoritmo BCrypt.
     * </p>
     *
     * @return codificador de contraseñas basado en BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configura la política de Cross-Origin Resource Sharing (CORS) utilizada
     * para controlar el acceso a la API desde aplicaciones externas.
     *
     * <p>
     * La configuración establece:
     * </p>
     * <ul>
     *     <li>Orígenes autorizados para consumir la API.</li>
     *     <li>Métodos HTTP permitidos.</li>
     *     <li>Cabeceras aceptadas y expuestas.</li>
     *     <li>Política de credenciales.</li>
     *     <li>Tiempo de almacenamiento en caché de la configuración.</li>
     * </ul>
     *
     * @return fuente de configuración CORS aplicable a todas las rutas expuestas
     *         por la aplicación.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost",
                "http://localhost:80",
                "http://localhost:3000",
                "http://localhost:5173"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}