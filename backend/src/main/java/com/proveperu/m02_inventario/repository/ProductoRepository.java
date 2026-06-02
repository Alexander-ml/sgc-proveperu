package com.proveperu.m02_inventario.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proveperu.m02_inventario.entity.Producto;
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

}