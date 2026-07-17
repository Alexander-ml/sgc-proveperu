package com.proveperu.testsupport;

import java.time.LocalDateTime;

import com.proveperu.m06_usuarios.entity.Permiso;
import com.proveperu.m06_usuarios.entity.Rol;
import com.proveperu.m06_usuarios.entity.RolPermiso;
import com.proveperu.m06_usuarios.entity.RolPermisoId;
import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.m06_usuarios.entity.UsuarioSesion;
import com.proveperu.m06_usuarios.enums.AccionPermiso;
import com.proveperu.m06_usuarios.enums.EstadoUsuario;
import com.proveperu.m06_usuarios.enums.ModuloPermiso;
import com.proveperu.shared.enums.EstadoActivoInactivo;
import com.proveperu.shared.enums.EstadoLogico;

/**
 * Fábrica de datos de prueba (fixtures) para el módulo de usuarios.
 *
 * <p>
 * Centraliza la construcción de entidades válidas, garantizando que todos
 * los campos NOT NULL heredados de las clases base de auditoría
 * ({@code estadoLogico}, {@code fechaHoraCreacion}) queden poblados, de
 * modo que las inserciones no fallen por restricciones de la base de datos.
 * </p>
 *
 * <p>
 * Las entidades se devuelven <em>sin persistir</em>; cada prueba decide
 * cómo guardarlas (repositorio o {@code TestEntityManager}).
 * </p>
 */
public final class UsuariosTestDataFactory {

    /** Marca temporal fija para hacer reproducibles las aserciones. */
    public static final LocalDateTime FECHA_BASE =
            LocalDateTime.of(2026, 1, 1, 8, 0, 0);

    private UsuariosTestDataFactory() {
    }

    public static Rol rol(String nombreRol) {
        Rol rol = Rol.builder()
                .nombreRol(nombreRol)
                .descripcion("Rol " + nombreRol)
                .estadoFisico(EstadoActivoInactivo.ACTIVO)
                .build();
        rol.setEstadoLogico(EstadoLogico.ACTIVO);
        rol.setFechaHoraCreacion(FECHA_BASE);
        return rol;
    }

    public static Usuario usuario(String nombreCompleto,
                                  String usuarioLogin,
                                  Rol rol,
                                  EstadoUsuario estado) {
        Usuario usuario = Usuario.builder()
                .nombreCompleto(nombreCompleto)
                .usuarioLogin(usuarioLogin)
                .passwordHash("$2a$12$hashDePrueba")
                .rol(rol)
                .estadoUsuario(estado)
                .build();
        usuario.setEstadoLogico(EstadoLogico.ACTIVO);
        usuario.setFechaHoraCreacion(FECHA_BASE);
        return usuario;
    }

    public static Permiso permiso(ModuloPermiso modulo, AccionPermiso accion) {
        Permiso permiso = Permiso.builder()
                .modulo(modulo)
                .accion(accion)
                .descripcion(modulo.name() + " - " + accion.name())
                .estadoFisico(EstadoActivoInactivo.ACTIVO)
                .build();
        permiso.setEstadoLogico(EstadoLogico.ACTIVO);
        permiso.setFechaHoraCreacion(FECHA_BASE);
        return permiso;
    }

    /**
     * Construye una asignación rol-permiso. Requiere que {@code rol} y
     * {@code permiso} ya estén persistidos (con id asignado), pues la clave
     * compuesta se deriva de sus identificadores.
     */
    public static RolPermiso rolPermiso(Rol rol, Permiso permiso) {
        RolPermiso rolPermiso = RolPermiso.builder()
                .id_rol_permiso(new RolPermisoId(rol.getIdRol(), permiso.getId()))
                .rol(rol)
                .permiso(permiso)
                .build();
        rolPermiso.setFechaHoraCreacion(FECHA_BASE);
        return rolPermiso;
    }

    public static UsuarioSesion sesion(Usuario usuario,
                                       LocalDateTime inicio,
                                       LocalDateTime fin) {
        return UsuarioSesion.builder()
                .usuario(usuario)
                .fechaHoraInicio(inicio)
                .fechaHoraFin(fin)
                .build();
    }
}
