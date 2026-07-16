package com.proveperu.m06_usuarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m06_usuarios.entity.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
     Optional<Rol> findByNombreRolIgnoreCase(String nombreRol);
}
