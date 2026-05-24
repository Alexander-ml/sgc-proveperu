package com.proveperu.security;

import lombok.RequiredArgsConstructor;
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

import java.util.List;

/**
 * Política de seguridad global del sistema*/
@Configuration
@EnableWebSecurity // Activa seguridad web
@EnableMethodSecurity // habilita @PreAuthorize en los controladores
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    // Cadena de filtros de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // Deshabilitar CSRF (usamos JWT, no cookies de sesión)
                .csrf(AbstractHttpConfigurer::disable)

                // Configurar CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos, no requieren token
                        .requestMatchers(
                                "/api/auth/**",
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()
                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )

                // Indica que la aplicación no mantendrá sesiones HTTP
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Proveedor de autenticación
                .authenticationProvider(authenticationProvider())

                // Agregar el filtro JWT antes del filtro de usuario y contraseña
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Proveedor de autenticación responsable de:
     * Validar credenciales
     * Integrarse con la fuente de datos
     * Construir el objeto Authentication válido*/
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /** Authentication Manager, Orquestador central de autenticación, encargado de:
     * Recibir solicitudes de autenticación
     * Delegar en uno o más AuthenticationProvider*/
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Hashea contraseñas antes de almacenarlas con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**Define la política de Cross-Origin Resource Sharing (CORS), permitiendo controlar:
     * Qué orígenes pueden consumir la API
     * Qué métodos HTTP están habilitados
     * Qué headers son aceptados y expuestos*/
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Orígenes Permitidos
        config.setAllowedOrigins(List.of(
                "http://localhost",
                "http://localhost:80",
                "http://localhost:3000",
                "http://localhost:5173"
        ));

        // Métodos HTTP Permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Headers Permitidos
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        // Headers Expuestos
        config.setExposedHeaders(List.of("Authorization"));
        // Credenciales
        config.setAllowCredentials(false);
        // Tiempo de Cacheo
        config.setMaxAge(3600L);

        // Aplica la configuración CORS a todas las rutas.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}