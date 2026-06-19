package com.proveperu.m03_compras.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m03_compras.entity.Proveedor;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
     /**
     * Lista los proveedores ordenados alfabéticamente
     * por razón social.
     */
    List<Proveedor> findAllByOrderByRazonSocialAsc();
}
