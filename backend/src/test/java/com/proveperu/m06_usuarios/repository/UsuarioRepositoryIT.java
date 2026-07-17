package com.proveperu.m06_usuarios.repository;

import static com.proveperu.testsupport.UsuariosTestDataFactory.rol;
import static com.proveperu.testsupport.UsuariosTestDataFactory.usuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import com.proveperu.m06_usuarios.entity.Rol;
import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.m06_usuarios.enums.EstadoUsuario;
import com.proveperu.testsupport.AbstractPostgresContainerTest;

/**
 * Pruebas de integración (Testcontainers + PostgreSQL real) para
 * {@link UsuarioRepository}.
 *
 * Cubre: consulta por login con {@code @EntityGraph}, conteo por estado,
 * filtro por nombre (incluyendo el caso de cadena vacía que devuelve todos)
 * y la restricción UNIQUE sobre {@code usuario_login}.
 */
class UsuarioRepositoryIT extends AbstractPostgresContainerTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager em;

    private Rol persistirRol(String nombre) {
        return em.persistFlushFind(rol(nombre));
    }

    @Test
    @DisplayName("findByUsuarioLogin: devuelve el usuario y su rol cuando existe")
    void findByUsuarioLogin_existente() {
        Rol rolAdmin = persistirRol("ADMINISTRADOR");
        em.persistAndFlush(usuario("Ana Pérez", "ana@x.com", rolAdmin, EstadoUsuario.ACTIVO));
        em.clear();

        Optional<Usuario> encontrado = usuarioRepository.findByUsuarioLogin("ana@x.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombreCompleto()).isEqualTo("Ana Pérez");
        // El @EntityGraph debe traer el rol cargado.
        assertThat(encontrado.get().getRol().getNombreRol()).isEqualTo("ADMINISTRADOR");
    }

    @Test
    @DisplayName("findByUsuarioLogin: vacío cuando no existe")
    void findByUsuarioLogin_inexistente() {
        assertThat(usuarioRepository.findByUsuarioLogin("nadie@x.com")).isEmpty();
    }

    @Test
    @DisplayName("usuario_login duplicado viola la restricción UNIQUE")
    void usuarioLoginDuplicado_violaUnique() {
        Rol rolAdmin = persistirRol("ADMINISTRADOR");
        em.persistAndFlush(usuario("Ana Pérez", "ana@x.com", rolAdmin, EstadoUsuario.ACTIVO));

        Usuario duplicado = usuario("Otra Ana", "ana@x.com", rolAdmin, EstadoUsuario.ACTIVO);

        assertThatThrownBy(() -> usuarioRepository.saveAndFlush(duplicado))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("countByEstadoUsuario: cuenta segregado por estado")
    void countByEstadoUsuario() {
        Rol rolAdmin = persistirRol("ADMINISTRADOR");
        em.persistAndFlush(usuario("Activo 1", "a1@x.com", rolAdmin, EstadoUsuario.ACTIVO));
        em.persistAndFlush(usuario("Activo 2", "a2@x.com", rolAdmin, EstadoUsuario.ACTIVO));
        em.persistAndFlush(usuario("Suspendido 1", "s1@x.com", rolAdmin, EstadoUsuario.SUSPENDIDO));

        assertThat(usuarioRepository.countByEstadoUsuario(EstadoUsuario.ACTIVO)).isEqualTo(2);
        assertThat(usuarioRepository.countByEstadoUsuario(EstadoUsuario.SUSPENDIDO)).isEqualTo(1);
    }

    @Test
    @DisplayName("findByNombreCompletoContainingIgnoreCase: filtra parcial e ignora mayúsculas")
    void findByNombre_filtra() {
        Rol rolAdmin = persistirRol("ADMINISTRADOR");
        em.persistAndFlush(usuario("Ana Pérez", "ana@x.com", rolAdmin, EstadoUsuario.ACTIVO));
        em.persistAndFlush(usuario("Beto Ruiz", "beto@x.com", rolAdmin, EstadoUsuario.ACTIVO));
        em.clear();

        List<Usuario> resultado =
                usuarioRepository.findByNombreCompletoContainingIgnoreCase("aNa");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUsuarioLogin()).isEqualTo("ana@x.com");
    }

    @Test
    @DisplayName("findByNombreCompletoContainingIgnoreCase(\"\"): devuelve todos")
    void findByNombre_vacioDevuelveTodos() {
        Rol rolAdmin = persistirRol("ADMINISTRADOR");
        em.persistAndFlush(usuario("Ana Pérez", "ana@x.com", rolAdmin, EstadoUsuario.ACTIVO));
        em.persistAndFlush(usuario("Beto Ruiz", "beto@x.com", rolAdmin, EstadoUsuario.ACTIVO));
        em.clear();

        assertThat(usuarioRepository.findByNombreCompletoContainingIgnoreCase("")).hasSize(2);
    }
}
