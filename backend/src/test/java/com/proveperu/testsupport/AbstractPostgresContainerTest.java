package com.proveperu.testsupport;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Clase base para las pruebas de integración de la capa de persistencia
 * del módulo de usuarios.
 *
 * <p>
 * Levanta un contenedor PostgreSQL real (mediante Testcontainers) y lo
 * comparte entre todas las clases de prueba que extiendan esta base
 * (patrón singleton: el contenedor se inicia una sola vez por JVM y
 * Ryuk lo destruye al finalizar). De esta forma se valida el
 * comportamiento real de las consultas, restricciones UNIQUE, claves
 * compuestas y transacciones contra el mismo motor usado en producción.
 * </p>
 *
 * <p>
 * Notas de diseño:
 * </p>
 * <ul>
 *     <li>{@code @AutoConfigureTestDatabase(replace = NONE)} evita que
 *     Spring sustituya el datasource por una base embebida.</li>
 *     <li>El esquema se genera con {@code ddl-auto=create-drop} a partir
 *     de las entidades JPA (no se usan los scripts SQL de producción),
 *     lo cual basta para verificar el contrato de los repositorios.</li>
 *     <li>Las propiedades del datasource se inyectan dinámicamente con
 *     {@link DynamicPropertySource}, evitando depender de variables de
 *     entorno como {@code DB_HOST} usadas por la configuración real.</li>
 * </ul>
 *
 * <p><strong>Requiere Docker disponible en la máquina que ejecuta las pruebas.</strong></p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractPostgresContainerTest {

    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("sgc_test")
                    .withUsername("test")
                    .withPassword("test");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect",
                () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.show-sql", () -> "false");
    }
}
