package com.proveperu.m06_usuarios.repository;

import static com.proveperu.testsupport.UsuariosTestDataFactory.rol;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import com.proveperu.testsupport.AbstractPostgresContainerTest;

/**
 * Pruebas de integración para {@link RolRepository}.
 * Cubre la búsqueda case-insensitive por nombre y la restricción UNIQUE
 * sobre {@code nombre_rol}.
 */
class RolRepositoryIT extends AbstractPostgresContainerTest {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("findByNombreRolIgnoreCase: encuentra ignorando mayúsculas/minúsculas")
    void findByNombreRolIgnoreCase_match() {
        em.persistAndFlush(rol("ADMINISTRADOR"));
        em.clear();

        assertThat(rolRepository.findByNombreRolIgnoreCase("administrador")).isPresent();
        assertThat(rolRepository.findByNombreRolIgnoreCase("AdMiNiStRaDoR")).isPresent();
    }

    @Test
    @DisplayName("findByNombreRolIgnoreCase: vacío cuando no existe")
    void findByNombreRolIgnoreCase_inexistente() {
        assertThat(rolRepository.findByNombreRolIgnoreCase("INEXISTENTE")).isEmpty();
    }

    @Test
    @DisplayName("nombre_rol duplicado viola la restricción UNIQUE")
    void nombreRolDuplicado_violaUnique() {
        em.persistAndFlush(rol("CAJERO"));

        assertThatThrownBy(() -> rolRepository.saveAndFlush(rol("CAJERO")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
