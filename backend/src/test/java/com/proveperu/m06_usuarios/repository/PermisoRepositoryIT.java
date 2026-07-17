package com.proveperu.m06_usuarios.repository;

import static com.proveperu.testsupport.UsuariosTestDataFactory.permiso;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.proveperu.m06_usuarios.entity.Permiso;
import com.proveperu.m06_usuarios.enums.AccionPermiso;
import com.proveperu.m06_usuarios.enums.ModuloPermiso;
import com.proveperu.testsupport.AbstractPostgresContainerTest;

/**
 * Pruebas de integración para {@link PermisoRepository}.
 * Verifica el CRUD básico y la correcta persistencia de los enums
 * {@code modulo}/{@code accion} mapeados como STRING.
 */
class PermisoRepositoryIT extends AbstractPostgresContainerTest {

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("save + findAll: persiste y recupera permisos con sus enums")
    void guardaYRecupera() {
        em.persistAndFlush(permiso(ModuloPermiso.VENTAS, AccionPermiso.CREAR));
        em.persistAndFlush(permiso(ModuloPermiso.INVENTARIO, AccionPermiso.LEER));
        em.clear();

        List<Permiso> permisos = permisoRepository.findAll();

        assertThat(permisos).hasSize(2);
        assertThat(permisos)
                .extracting(p -> p.getModulo().name() + " - " + p.getAccion().name())
                .containsExactlyInAnyOrder("VENTAS - CREAR", "INVENTARIO - LEER");
    }

    @Test
    @DisplayName("findById: recupera el permiso por su id generado")
    void findById() {
        Permiso guardado = em.persistFlushFind(permiso(ModuloPermiso.CAJA, AccionPermiso.ACTUALIZAR));
        em.clear();

        assertThat(permisoRepository.findById(guardado.getId())).isPresent();
    }
}
