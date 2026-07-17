package com.proveperu.m06_usuarios.repository;

import static com.proveperu.testsupport.UsuariosTestDataFactory.permiso;
import static com.proveperu.testsupport.UsuariosTestDataFactory.rol;
import static com.proveperu.testsupport.UsuariosTestDataFactory.rolPermiso;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.proveperu.m06_usuarios.entity.Permiso;
import com.proveperu.m06_usuarios.entity.Rol;
import com.proveperu.m06_usuarios.entity.RolPermisoId;
import com.proveperu.m06_usuarios.enums.AccionPermiso;
import com.proveperu.m06_usuarios.enums.ModuloPermiso;
import com.proveperu.testsupport.AbstractPostgresContainerTest;

/**
 * Pruebas de integración para {@link RolPermisoRepository}, centradas en el
 * comportamiento de la <strong>clave primaria compuesta</strong>
 * ({@link RolPermisoId}) y las operaciones {@code findByRol} / {@code deleteByRol}.
 */
class RolPermisoRepositoryIT extends AbstractPostgresContainerTest {

    @Autowired
    private RolPermisoRepository rolPermisoRepository;

    @Autowired
    private TestEntityManager em;

    private Rol rolGuardado;
    private Permiso permisoGuardado;

    private void prepararRolYPermiso() {
        rolGuardado = em.persistFlushFind(rol("ADMINISTRADOR"));
        permisoGuardado = em.persistFlushFind(permiso(ModuloPermiso.VENTAS, AccionPermiso.CREAR));
    }

    @Test
    @DisplayName("Persiste y recupera por clave compuesta (findById + findByRol)")
    void guardaYRecuperaPorClaveCompuesta() {
        prepararRolYPermiso();

        rolPermisoRepository.save(rolPermiso(rolGuardado, permisoGuardado));
        em.flush();
        em.clear();

        RolPermisoId id = new RolPermisoId(rolGuardado.getIdRol(), permisoGuardado.getId());
        assertThat(rolPermisoRepository.findById(id)).isPresent();
        assertThat(rolPermisoRepository.findByRol(rolGuardado)).hasSize(1);
    }

    @Test
    @DisplayName("deleteByRol: elimina todas las asignaciones del rol")
    void deleteByRol() {
        prepararRolYPermiso();
        Permiso segundo = em.persistFlushFind(permiso(ModuloPermiso.INVENTARIO, AccionPermiso.LEER));

        rolPermisoRepository.save(rolPermiso(rolGuardado, permisoGuardado));
        rolPermisoRepository.save(rolPermiso(rolGuardado, segundo));
        em.flush();
        assertThat(rolPermisoRepository.findByRol(rolGuardado)).hasSize(2);

        rolPermisoRepository.deleteByRol(rolGuardado);
        em.flush();
        em.clear();

        assertThat(rolPermisoRepository.findByRol(rolGuardado)).isEmpty();
    }

    @Test
    @DisplayName("save con la misma clave compuesta dos veces resulta en una sola fila (merge)")
    void claveCompuestaDuplicada_resultaEnUnaFila() {
        prepararRolYPermiso();

        rolPermisoRepository.save(rolPermiso(rolGuardado, permisoGuardado));
        // Mismo (idRol, idPermiso): save() actúa como merge -> no inserta una segunda fila.
        rolPermisoRepository.save(rolPermiso(rolGuardado, permisoGuardado));
        em.flush();
        em.clear();

        assertThat(rolPermisoRepository.findByRol(rolGuardado)).hasSize(1);
    }
}
