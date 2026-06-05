package com.proveperu.m06_usuarios.repository;

import com.proveperu.m06_usuarios.entity.UsuarioSesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioSesionRepository extends JpaRepository<UsuarioSesion, Integer> {
}
