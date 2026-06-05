package com.proveperu.m06_usuarios.repository;

import com.proveperu.m06_usuarios.entity.RolPermiso;
import com.proveperu.m06_usuarios.entity.RolPermisoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, RolPermisoId> {
}
