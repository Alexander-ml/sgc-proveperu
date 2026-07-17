package com.proveperu.m06_usuarios.service;

import static com.proveperu.testsupport.UsuariosTestDataFactory.permiso;
import static com.proveperu.testsupport.UsuariosTestDataFactory.rol;
import static com.proveperu.testsupport.UsuariosTestDataFactory.rolPermiso;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.proveperu.m06_usuarios.dto.request.ActualizarPermisosRolRequest;
import com.proveperu.m06_usuarios.entity.Permiso;
import com.proveperu.m06_usuarios.entity.Rol;
import com.proveperu.m06_usuarios.enums.AccionPermiso;
import com.proveperu.m06_usuarios.enums.ModuloPermiso;
import com.proveperu.m06_usuarios.repository.PermisoRepository;
import com.proveperu.m06_usuarios.repository.RolPermisoRepository;
import com.proveperu.m06_usuarios.repository.RolRepository;
import com.proveperu.testsupport.AbstractPostgresContainerTest;

/**
 * Pruebas de integración de {@link RolService} contra PostgreSQL real,
 * enfocadas en dos comportamientos que solo se observan con base de datos
 * y transacciones reales:
 *
 * <ol>
 *     <li><strong>Atomicidad</strong> de {@code actualizarPermisosRol}
 *     ({@code @Transactional}): si un permiso de la lista no existe, el
 *     {@code deleteByRol} y los {@code save} previos deben revertirse.</li>
 *     <li><strong>Lazy loading</strong>: {@code obtenerRolPorId} accede a la
 *     colección perezosa {@code rolPermisos} sin transacción/sesión abierta
 *     (no es {@code @Transactional}), lo que provoca
 *     {@link LazyInitializationException} cuando no actúa Open-Session-In-View.</li>
 * </ol>
 *
 * <p>
 * La clase se anota con {@code @Transactional(NOT_SUPPORTED)} para desactivar
 * la transacción de rollback automática de {@code @DataJpaTest} y así poder
 * observar los commits/rollbacks reales del servicio.
 * </p>
 */
@Import(RolService.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class RolServicePersistenceIT extends AbstractPostgresContainerTest {

    @Autowired
    private RolService rolService;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private RolPermisoRepository rolPermisoRepository;

    @BeforeEach
    void limpiar() {
        rolPermisoRepository.deleteAll();
        permisoRepository.deleteAll();
        rolRepository.deleteAll();
    }

    @Test
    @DisplayName("actualizarPermisosRol revierte todo si un permiso de la lista no existe")
    void rollbackCuandoUnPermisoNoExiste() {
        Rol rolGuardado = rolRepository.save(rol("ADMINISTRADOR"));
        Permiso permisoValido =
                permisoRepository.save(permiso(ModuloPermiso.VENTAS, AccionPermiso.CREAR));
        rolPermisoRepository.save(rolPermiso(rolGuardado, permisoValido));
        assertThat(rolPermisoRepository.findByRol(rolGuardado)).hasSize(1);

        ActualizarPermisosRolRequest request = new ActualizarPermisosRolRequest();
        request.setPermisos(List.of(permisoValido.getId(), 999_999));

        assertThatThrownBy(() ->
                rolService.actualizarPermisosRol(rolGuardado.getIdRol(), request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Permiso no encontrado");

        // Rollback: la asignación original sigue intacta (no se vació el rol).
        assertThat(rolPermisoRepository.findByRol(rolGuardado)).hasSize(1);
    }

    @Test
    @DisplayName("actualizarPermisosRol reemplaza correctamente cuando todos los permisos existen")
    void reemplazoExitoso() {
        Rol rolGuardado = rolRepository.save(rol("CAJERO"));
        Permiso p1 = permisoRepository.save(permiso(ModuloPermiso.VENTAS, AccionPermiso.CREAR));
        Permiso p2 = permisoRepository.save(permiso(ModuloPermiso.CAJA, AccionPermiso.LEER));

        ActualizarPermisosRolRequest request = new ActualizarPermisosRolRequest();
        request.setPermisos(List.of(p1.getId(), p2.getId()));

        rolService.actualizarPermisosRol(rolGuardado.getIdRol(), request);

        assertThat(rolPermisoRepository.findByRol(rolGuardado)).hasSize(2);
    }

    @Test
    @DisplayName("obtenerRolPorId lanza LazyInitializationException sin sesión abierta (depende de OSIV)")
    void obtenerRolPorId_lazyFueraDeTransaccion() {
        Rol rolGuardado = rolRepository.save(rol("ALMACENERO"));
        Permiso permisoGuardado =
                permisoRepository.save(permiso(ModuloPermiso.INVENTARIO, AccionPermiso.LEER));
        rolPermisoRepository.save(rolPermiso(rolGuardado, permisoGuardado));

        // El método no es @Transactional: al recorrer rolPermisos (LAZY) tras
        // cerrarse la sesión del findById, Hibernate falla.
        assertThatThrownBy(() -> rolService.obtenerRolPorId(rolGuardado.getIdRol()))
                .isInstanceOf(LazyInitializationException.class);
    }
}
