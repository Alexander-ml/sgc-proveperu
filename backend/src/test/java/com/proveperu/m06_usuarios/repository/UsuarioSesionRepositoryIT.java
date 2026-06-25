package com.proveperu.m06_usuarios.repository;

import static com.proveperu.testsupport.UsuariosTestDataFactory.rol;
import static com.proveperu.testsupport.UsuariosTestDataFactory.sesion;
import static com.proveperu.testsupport.UsuariosTestDataFactory.usuario;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.proveperu.m06_usuarios.entity.Rol;
import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.m06_usuarios.entity.UsuarioSesion;
import com.proveperu.m06_usuarios.enums.EstadoUsuario;
import com.proveperu.testsupport.AbstractPostgresContainerTest;

/**
 * Pruebas de integración para {@link UsuarioSesionRepository}.
 * Verifica el orden descendente del historial y la consulta de la última
 * sesión abierta (usada por el logout en {@code AuthService}).
 */
class UsuarioSesionRepositoryIT extends AbstractPostgresContainerTest {

    @Autowired
    private UsuarioSesionRepository usuarioSesionRepository;

    @Autowired
    private TestEntityManager em;

    private Usuario usuarioGuardado;

    private void prepararUsuario() {
        Rol rolAdmin = em.persistFlushFind(rol("ADMINISTRADOR"));
        usuarioGuardado = em.persistFlushFind(
                usuario("Ana Pérez", "ana@x.com", rolAdmin, EstadoUsuario.ACTIVO));
    }

    @Test
    @DisplayName("findAllByOrderByFechaHoraInicioDesc: ordena de más reciente a más antigua")
    void historialOrdenadoDesc() {
        prepararUsuario();
        LocalDateTime t1 = LocalDateTime.of(2026, 1, 1, 8, 0);
        LocalDateTime t2 = LocalDateTime.of(2026, 1, 2, 8, 0);
        LocalDateTime t3 = LocalDateTime.of(2026, 1, 3, 8, 0);
        em.persistAndFlush(sesion(usuarioGuardado, t1, null));
        em.persistAndFlush(sesion(usuarioGuardado, t3, null));
        em.persistAndFlush(sesion(usuarioGuardado, t2, null));
        em.clear();

        List<UsuarioSesion> sesiones =
                usuarioSesionRepository.findAllByOrderByFechaHoraInicioDesc();

        assertThat(sesiones)
                .extracting(UsuarioSesion::getFechaHoraInicio)
                .containsExactly(t3, t2, t1);
    }

    @Test
    @DisplayName("findFirst...FechaHoraFinIsNull...: devuelve la última sesión abierta del usuario")
    void ultimaSesionAbierta() {
        prepararUsuario();
        LocalDateTime t1 = LocalDateTime.of(2026, 1, 1, 8, 0);
        LocalDateTime t2 = LocalDateTime.of(2026, 1, 2, 8, 0);
        // Sesión antigua ya cerrada.
        em.persistAndFlush(sesion(usuarioGuardado, t1, t1.plusHours(2)));
        // Sesión reciente aún abierta.
        em.persistAndFlush(sesion(usuarioGuardado, t2, null));
        em.clear();

        Optional<UsuarioSesion> abierta = usuarioSesionRepository
                .findFirstByUsuarioAndFechaHoraFinIsNullOrderByFechaHoraInicioDesc(usuarioGuardado);

        assertThat(abierta).isPresent();
        assertThat(abierta.get().getFechaHoraInicio()).isEqualTo(t2);
        assertThat(abierta.get().getFechaHoraFin()).isNull();
    }

    @Test
    @DisplayName("findFirst...FechaHoraFinIsNull...: vacío cuando todas están cerradas")
    void sinSesionAbierta() {
        prepararUsuario();
        LocalDateTime t1 = LocalDateTime.of(2026, 1, 1, 8, 0);
        em.persistAndFlush(sesion(usuarioGuardado, t1, t1.plusHours(1)));
        em.clear();

        assertThat(usuarioSesionRepository
                .findFirstByUsuarioAndFechaHoraFinIsNullOrderByFechaHoraInicioDesc(usuarioGuardado))
                .isEmpty();
    }
}
