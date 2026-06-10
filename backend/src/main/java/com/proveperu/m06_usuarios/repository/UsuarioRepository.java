package com.proveperu.m06_usuarios.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m06_usuarios.entity.Usuario;
import com.proveperu.m06_usuarios.enums.EstadoUsuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @EntityGraph(attributePaths = {"rol"})
    Optional<Usuario> findByUsuarioLogin(String usuarioLogin);
    long countByEstadoFisico(EstadoUsuario estadoFisico);
    @EntityGraph(attributePaths = {"rol"})
    List<Usuario> findByNombreCompletoContainingIgnoreCase(String nombreCompleto);
}
