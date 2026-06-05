package com.proveperu.m01_ventas.repository;

import com.proveperu.m01_ventas.entity.DetalleVenta;
import com.proveperu.m01_ventas.entity.DetalleVentaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, DetalleVentaId> {

}
